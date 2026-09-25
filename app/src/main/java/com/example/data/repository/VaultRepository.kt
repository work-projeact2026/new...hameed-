package com.example.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.core.security.CryptoManager
import com.example.data.local.dao.VaultMediaDao
import com.example.data.local.entity.VaultMediaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class VaultRepository(
    private val vaultMediaDao: VaultMediaDao,
    private val cryptoManager: CryptoManager,
    private val context: Context
) {
    private val vaultDir = File(context.filesDir, "vault").apply {
        if (!exists()) mkdirs()
    }
    private val decryptedCacheDir = File(context.cacheDir, "vault_decrypted").apply {
        if (!exists()) mkdirs()
    }

    fun getAllMedia(): Flow<List<VaultMediaEntity>> = vaultMediaDao.getAllMedia()

    fun getMediaByKind(kind: String): Flow<List<VaultMediaEntity>> = vaultMediaDao.getMediaByKind(kind)

    suspend fun getMediaById(id: String): VaultMediaEntity? = vaultMediaDao.getMediaById(id)

    suspend fun saveEncryptedMedia(
        title: String,
        sourceBytes: ByteArray,
        mediaKind: String,
        durationMs: Long = 0L
    ): VaultMediaEntity = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val extension = when (mediaKind) {
            "PHOTO" -> "jpg"
            "VIDEO" -> "mp4"
            "AUDIO" -> "m4a"
            else -> "bin"
        }
        val encryptedFile = File(vaultDir, "$id.enc")
        val encryptedBytes = cryptoManager.encryptBytes(sourceBytes)
        encryptedFile.outputStream().use { it.write(encryptedBytes) }

        val entity = VaultMediaEntity(
            id = id,
            title = title,
            filePath = encryptedFile.absolutePath,
            mediaKind = mediaKind,
            durationMs = durationMs,
            fileSize = encryptedFile.length(),
            createdAt = System.currentTimeMillis(),
            isEncrypted = true
        )
        vaultMediaDao.insertMedia(entity)
        entity
    }

    suspend fun importFromUri(uri: Uri, title: String, kind: String): VaultMediaEntity? = withContext(Dispatchers.IO) {
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
            saveEncryptedMedia(title = title, sourceBytes = bytes, mediaKind = kind)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun decryptToCache(entity: VaultMediaEntity): File = withContext(Dispatchers.IO) {
        val extension = when (entity.mediaKind) {
            "PHOTO" -> "jpg"
            "VIDEO" -> "mp4"
            "AUDIO" -> "m4a"
            else -> "bin"
        }
        val cachedFile = File(decryptedCacheDir, "${entity.id}_dec.$extension")
        if (!cachedFile.exists() || cachedFile.length() == 0L) {
            val encryptedFile = File(entity.filePath)
            cryptoManager.decryptFile(encryptedFile, cachedFile)
        }
        cachedFile
    }

    suspend fun renameMedia(id: String, newTitle: String) {
        val media = vaultMediaDao.getMediaById(id)
        if (media != null) {
            vaultMediaDao.updateMedia(media.copy(title = newTitle))
        }
    }

    suspend fun deleteMedia(id: String) = withContext(Dispatchers.IO) {
        val media = vaultMediaDao.getMediaById(id)
        if (media != null) {
            File(media.filePath).delete()
            File(decryptedCacheDir, "${media.id}_dec.jpg").delete()
            File(decryptedCacheDir, "${media.id}_dec.mp4").delete()
            File(decryptedCacheDir, "${media.id}_dec.m4a").delete()
            vaultMediaDao.deleteMediaById(id)
        }
    }

    suspend fun clearDecryptedCache() = withContext(Dispatchers.IO) {
        decryptedCacheDir.listFiles()?.forEach { it.delete() }
    }

    suspend fun exportMedia(entity: VaultMediaEntity, targetUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val cachedFile = decryptToCache(entity)
            context.contentResolver.openOutputStream(targetUri)?.use { out ->
                cachedFile.inputStream().use { input ->
                    input.copyTo(out)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun exportToPublicGallery(entity: VaultMediaEntity): Uri? = withContext(Dispatchers.IO) {
        try {
            val cachedFile = decryptToCache(entity)
            val resolver = context.contentResolver
            val fileName = "${entity.title.replace(" ", "_")}_${System.currentTimeMillis()}"

            val contentValues = ContentValues().apply {
                when (entity.mediaKind) {
                    "PHOTO" -> {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SecureLens")
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }
                    "VIDEO" -> {
                        put(MediaStore.Video.Media.DISPLAY_NAME, "$fileName.mp4")
                        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/SecureLens")
                            put(MediaStore.Video.Media.IS_PENDING, 1)
                        }
                    }
                    "AUDIO" -> {
                        put(MediaStore.Audio.Media.DISPLAY_NAME, "$fileName.m4a")
                        put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/SecureLens")
                            put(MediaStore.Audio.Media.IS_PENDING, 1)
                        }
                    }
                    else -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Downloads.DISPLAY_NAME, "$fileName.bin")
                            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/SecureLens")
                            put(MediaStore.Downloads.IS_PENDING, 1)
                        }
                    }
                }
            }

            val collectionUri = when (entity.mediaKind) {
                "PHOTO" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "VIDEO" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "AUDIO" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Downloads.EXTERNAL_CONTENT_URI else MediaStore.Files.getContentUri("external")
            }

            val itemUri = resolver.insert(collectionUri, contentValues) ?: return@withContext null
            resolver.openOutputStream(itemUri)?.use { out ->
                cachedFile.inputStream().use { input ->
                    input.copyTo(out)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                when (entity.mediaKind) {
                    "PHOTO" -> contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    "VIDEO" -> contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                    "AUDIO" -> contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    else -> contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                }
                resolver.update(itemUri, contentValues, null, null)
            }
            itemUri
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getShareableUri(entity: VaultMediaEntity): Uri? = withContext(Dispatchers.IO) {
        try {
            val cachedFile = decryptToCache(entity)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cachedFile
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getShareableFileUri(file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getStorageBreakdown(): Triple<Long, Long, Long> = withContext(Dispatchers.IO) {
        // Returns (photosBytes, videosBytes, audioBytes)
        var photos = 0L
        var videos = 0L
        var audio = 0L
        vaultDir.listFiles()?.forEach { file ->
            // query db to classify
        }
        Triple(photos, videos, audio)
    }
}
