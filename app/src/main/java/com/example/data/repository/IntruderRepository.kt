package com.example.data.repository

import com.example.data.local.dao.IntruderEventDao
import com.example.data.local.entity.IntruderEventEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class IntruderRepository(private val intruderDao: IntruderEventDao) {
    fun getAllEvents(): Flow<List<IntruderEventEntity>> = intruderDao.getAllEvents()

    suspend fun recordEvent(
        failedCount: Int,
        source: String = "Device Admin callback",
        threshold: Int = 3,
        hasPhoto: Boolean = false,
        photoPath: String? = null,
        note: String = if (hasPhoto) "Front camera capture" else "Photo unavailable (OS protected)"
    ) {
        val event = IntruderEventEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            failedCount = failedCount,
            source = source,
            threshold = threshold,
            note = note,
            hasPhoto = hasPhoto,
            photoPath = photoPath
        )
        intruderDao.insertEvent(event)
    }

    suspend fun deleteEvent(id: String, photoPath: String? = null) {
        if (photoPath != null) {
            try {
                val file = java.io.File(photoPath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {}
        }
        intruderDao.deleteEventById(id)
    }

    suspend fun clearAll() {
        intruderDao.clearAll()
    }
}
