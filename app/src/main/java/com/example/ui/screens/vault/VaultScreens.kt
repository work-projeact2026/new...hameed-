package com.example.ui.screens.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SecureLensApp
import com.example.data.local.entity.VaultMediaEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun VaultUnlockScreen(
    onUnlockSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val settingsStore = SecureLensApp.instance.settingsStore
    val coroutineScope = rememberCoroutineScope()
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var storedPin by remember { mutableStateOf<String?>(null) }
    var isCheckingPin by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val pin = settingsStore.vaultPin.first()
        val enabled = settingsStore.isVaultPasswordEnabled.first()
        storedPin = pin
        if (!enabled || pin.isNullOrBlank()) {
            // Password not configured or disabled: open vault directly
            onUnlockSuccess()
        } else {
            isCheckingPin = false
        }
    }

    if (isCheckingPin) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SecureBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SecurePrimary)
        }
        return
    }

    fun handleDigit(d: String) {
        if (enteredPin.length < 6) {
            val next = enteredPin + d
            enteredPin = next
            if (next.length == 6) {
                if (storedPin == null || storedPin == next) {
                    onUnlockSuccess()
                } else {
                    errorMessage = "Incorrect PIN. Please try again."
                    enteredPin = ""
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader(
            title = "Private Vault",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SecurePrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = SecurePrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter 6-Digit Vault PIN",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SecureInk
            )
        )
        Text(
            text = "Keep your recordings and logs secured with local AES encryption.",
            style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PIN dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                val filled = i < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (filled) SecurePrimary else SecureSurfaceSoft)
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage ?: "",
                color = SecureAlert,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Numeric Keypad
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in keys) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    for (k in row) {
                        if (k.isEmpty()) {
                            Spacer(modifier = Modifier.size(64.dp))
                        } else if (k == "DEL") {
                            IconButton(
                                onClick = {
                                    if (enteredPin.isNotEmpty()) {
                                        enteredPin = enteredPin.dropLast(1)
                                        errorMessage = null
                                    }
                                },
                                modifier = Modifier
                                    .size(64.dp)
                                    .testTag("key_del")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "Delete",
                                    tint = SecureInk
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable { handleDigit(k) }
                                    .testTag("key_$k"),
                                shape = CircleShape,
                                color = SecureSurface
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = k,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SecureInk
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onUnlockSuccess,
            modifier = Modifier.testTag("btn_skip_unlock")
        ) {
            Text("Open Vault (Default / Demo Access)", color = SecurePrimary)
        }
    }
}

@Composable
fun VaultGalleryScreen(
    onMediaClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onSecuritySettingsClick: () -> Unit
) {
    val vaultRepo = SecureLensApp.instance.vaultRepository
    val mediaList by vaultRepo.getAllMedia().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = "Encrypted Vault",
            onBackClick = onBackClick,
            actionIcon = Icons.Default.Security,
            actionContentDescription = "Vault Security",
            onActionClick = onSecuritySettingsClick
        )

        if (mediaList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SecureMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your Vault is Empty",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecureInk
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Encrypted video recordings, audio memos and intruder captures will appear here.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mediaList) { item ->
                    VaultMediaCard(item = item, onClick = { onMediaClick(item.id) })
                }
            }
        }
    }
}

@Composable
private fun VaultMediaCard(
    item: VaultMediaEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SecureSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SecureSurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.mediaKind) {
                        "VIDEO" -> Icons.Default.Videocam
                        "AUDIO" -> Icons.Default.Mic
                        else -> Icons.Default.Photo
                    },
                    contentDescription = null,
                    tint = SecurePrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SecureInk
                ),
                maxLines = 1
            )
            Text(
                text = item.mediaKind,
                style = MaterialTheme.typography.labelSmall.copy(color = SecureMuted)
            )
        }
    }
}

@Composable
fun MediaViewerScreen(
    mediaId: String,
    onBackClick: () -> Unit,
    onDeleted: () -> Unit
) {
    val vaultRepo = SecureLensApp.instance.vaultRepository
    var mediaItem by remember { mutableStateOf<VaultMediaEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mediaId) {
        mediaItem = vaultRepo.getMediaById(mediaId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = mediaItem?.title ?: "Encrypted Media",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = SecurePrimary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Encrypted ${mediaItem?.mediaKind ?: "Asset"}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Protected with AES-GCM 256 hardware keystore",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        vaultRepo.deleteMedia(mediaId)
                        onDeleted()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SecureAlert),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete From Vault")
            }
        }
    }
}

@Composable
fun VaultSecurityScreen(
    onBackClick: () -> Unit,
    onLockVault: () -> Unit
) {
    val settingsStore = SecureLensApp.instance.settingsStore
    val isBiometric by settingsStore.isBiometricEnabled.collectAsState(initial = true)
    val isPasswordEnabled by settingsStore.isVaultPasswordEnabled.collectAsState(initial = false)
    val currentPin by settingsStore.vaultPin.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    var showPinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var pinDialogError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = "Vault Security",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecureSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Vault Password Protection",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SecureInk
                                )
                            )
                            Text(
                                text = if (isPasswordEnabled) "PIN required to access Private Vault" else "Disabled — Vault opens directly without PIN",
                                style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                            )
                        }
                        Switch(
                            checked = isPasswordEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    newPinInput = ""
                                    pinDialogError = null
                                    showPinDialog = true
                                } else {
                                    coroutineScope.launch {
                                        settingsStore.setVaultPasswordEnabled(false)
                                    }
                                }
                            }
                        )
                    }

                    if (isPasswordEnabled) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = SecureDivider
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    newPinInput = ""
                                    pinDialogError = null
                                    showPinDialog = true
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Change 6-Digit PIN",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SecurePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = SecurePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecureSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Biometric Authentication",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SecureInk
                                )
                            )
                            Text(
                                text = "Unlock with fingerprint or face sensor",
                                style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                            )
                        }
                        Switch(
                            checked = isBiometric,
                            onCheckedChange = { checked ->
                                coroutineScope.launch {
                                    settingsStore.setBiometricEnabled(checked)
                                }
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onLockVault,
                colors = ButtonDefaults.buttonColors(containerColor = SecurePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lock Vault Immediately")
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Text(
                    text = if (currentPin.isNullOrBlank()) "Set Vault PIN" else "Change Vault PIN",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter a 6-digit numeric PIN to secure your Private Vault:",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) newPinInput = it },
                        label = { Text("6-Digit PIN") },
                        singleLine = true,
                        isError = pinDialogError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinDialogError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pinDialogError ?: "",
                            color = SecureAlert,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length == 6) {
                            coroutineScope.launch {
                                settingsStore.setVaultPin(newPinInput)
                                showPinDialog = false
                            }
                        } else {
                            pinDialogError = "PIN must be exactly 6 digits."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecurePrimary)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel", color = SecureMuted)
                }
            },
            containerColor = SecureSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
