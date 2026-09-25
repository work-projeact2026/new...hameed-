package com.example.ui.screens.onboarding

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.receiver.SecurityAdminReceiver
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun PromotionScreen(
    onContinueFree: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SecureSurfaceSoft)
                    .clickable { onContinueFree() }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("btn_skip_promo")
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SecurePrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "More Tools.\nMore Control.",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SecureInk,
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                lineHeight = 36.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Explore optional advanced features when available.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SecureMuted,
                fontSize = 14.sp
            )
        )

        Spacer(modifier = Modifier.weight(0.4f))

        ScannerConcentricRings(
            centerIcon = Icons.Outlined.Star,
            isScanning = false,
            size = 260.dp
        )

        Spacer(modifier = Modifier.weight(0.6f))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SecureSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "SecureLens Premium",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Compare plans and terms before purchasing. Nothing is charged from this preview.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecureMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryGradientButton(
            text = "Continue with Free",
            onClick = onContinueFree,
            testTag = "btn_continue_free"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No subscription is required to finish setup.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SecureMuted.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

enum class PermissionDisplayStatus {
    GRANTED,
    NOT_GRANTED,
    REQUIRES_SETUP,
    UNAVAILABLE
}

@Composable
fun PermissionsSetupScreen(
    onRequestPermission: ((String) -> Unit)? = null,
    onContinue: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val dpm = remember { context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager }
    val adminComponent = remember { ComponentName(context, SecurityAdminReceiver::class.java) }

    // Dynamic states for each permission
    var cameraGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var audioGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    var notifGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }
    var locationGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
    var bluetoothGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    var adminActive by remember {
        mutableStateOf(dpm?.isAdminActive(adminComponent) == true)
    }

    // Hardware availability
    val hasCameraHardware = remember { pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) }
    val hasMicHardware = remember { pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE) }
    val hasBtHardware = remember { pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) }
    val hasWifiHardware = remember { pm.hasSystemFeature(PackageManager.FEATURE_WIFI) }

    // Launchers for individual permissions
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        cameraGranted = it
    }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        audioGranted = it
    }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        notifGranted = it
    }
    val locationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        locationGranted = it
    }
    val bluetoothLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        bluetoothGranted = map.values.any { it }
    }
    val adminLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        adminActive = dpm?.isAdminActive(adminComponent) == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        if (onBackClick != null) {
            AppHeader(
                title = "Permissions & Access",
                onBackClick = onBackClick
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (onBackClick == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "System Permissions",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk,
                        fontSize = 26.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Authorize only the tools you intend to use. Permissions can be managed individually anytime.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecureMuted,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            // 1. Camera Access
            PermissionStatusCard(
                title = "Camera Access",
                description = "Required for optical lens glint scanner, live viewfinder, and encrypted recording.",
                icon = Icons.Outlined.CameraAlt,
                status = if (!hasCameraHardware) PermissionDisplayStatus.UNAVAILABLE else if (cameraGranted) PermissionDisplayStatus.GRANTED else PermissionDisplayStatus.NOT_GRANTED,
                onAction = {
                    if (onRequestPermission != null) onRequestPermission(Manifest.permission.CAMERA)
                    else cameraLauncher.launch(Manifest.permission.CAMERA)
                },
                testTag = "perm_camera"
            )

            // 2. Microphone
            PermissionStatusCard(
                title = "Microphone & Audio",
                description = "Enables audio track recording with video sessions and encrypted voice notes.",
                icon = Icons.Outlined.Mic,
                status = if (!hasMicHardware) PermissionDisplayStatus.UNAVAILABLE else if (audioGranted) PermissionDisplayStatus.GRANTED else PermissionDisplayStatus.NOT_GRANTED,
                onAction = {
                    if (onRequestPermission != null) onRequestPermission(Manifest.permission.RECORD_AUDIO)
                    else audioLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                testTag = "perm_audio"
            )

            // 3. Notifications
            PermissionStatusCard(
                title = "Persistent Notifications",
                description = "Displays ongoing foreground recording timer and unauthorized intruder alerts.",
                icon = Icons.Outlined.Notifications,
                status = if (notifGranted) PermissionDisplayStatus.GRANTED else PermissionDisplayStatus.NOT_GRANTED,
                onAction = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (onRequestPermission != null) onRequestPermission(Manifest.permission.POST_NOTIFICATIONS)
                        else notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                testTag = "perm_notifications"
            )

            // 4. Bluetooth & Nearby Scanning
            PermissionStatusCard(
                title = "Bluetooth & Nearby Devices",
                description = "Discovers unknown Bluetooth beacons, BLE peripherals, and wireless trackers.",
                icon = Icons.Outlined.Bluetooth,
                status = if (!hasBtHardware) PermissionDisplayStatus.UNAVAILABLE else if (bluetoothGranted) PermissionDisplayStatus.GRANTED else PermissionDisplayStatus.NOT_GRANTED,
                onAction = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        bluetoothLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT))
                    } else {
                        if (onRequestPermission != null) onRequestPermission(Manifest.permission.BLUETOOTH)
                        else bluetoothLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN))
                    }
                },
                testTag = "perm_bluetooth"
            )

            // 5. Wi-Fi & Network Discovery
            PermissionStatusCard(
                title = "Wi-Fi & Local Network",
                description = "Android requires location permission to scan nearby Wi-Fi APs and detect RTSP/IP cameras.",
                icon = Icons.Outlined.Wifi,
                status = if (!hasWifiHardware) PermissionDisplayStatus.UNAVAILABLE else if (locationGranted) PermissionDisplayStatus.GRANTED else PermissionDisplayStatus.NOT_GRANTED,
                onAction = {
                    if (onRequestPermission != null) onRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    else locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                testTag = "perm_location"
            )

            // 6. Device Administrator (Intruder Guard)
            PermissionStatusCard(
                title = "Device Administrator",
                description = "Authorizes the Android system callback to log failed device unlock attempts.",
                icon = Icons.Outlined.AdminPanelSettings,
                status = if (adminActive) PermissionDisplayStatus.GRANTED else PermissionDisplayStatus.REQUIRES_SETUP,
                actionLabel = "Setup",
                onAction = {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "SecureLens logs failed unlock attempts.")
                    }
                    adminLauncher.launch(intent)
                },
                testTag = "perm_admin"
            )

            // 7. Foreground Service & Lock-Screen Status
            PermissionStatusCard(
                title = "Foreground Service & Lock Screen",
                description = "Keeps active user-started recordings alive when the screen turns off or locks.",
                icon = Icons.Outlined.LockClock,
                status = PermissionDisplayStatus.GRANTED,
                onAction = {},
                testTag = "perm_foreground"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Open System Settings button if user denied or wants to manage system settings
            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SecurePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_app_settings")
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Android App Settings", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (onContinue != null) {
                PrimaryGradientButton(
                    text = "Continue",
                    onClick = onContinue,
                    testTag = "btn_continue_permissions"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PermissionStatusCard(
    title: String,
    description: String,
    icon: ImageVector,
    status: PermissionDisplayStatus,
    actionLabel: String = "Grant",
    onAction: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecureSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (status) {
                                PermissionDisplayStatus.GRANTED -> SecureSoftGreen
                                PermissionDisplayStatus.REQUIRES_SETUP -> SecureSoftAlert
                                else -> SecureSurfaceSoft
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = when (status) {
                            PermissionDisplayStatus.GRANTED -> SecureSuccess
                            PermissionDisplayStatus.REQUIRES_SETUP -> SecureAlert
                            else -> SecurePrimary
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecureInk,
                            fontSize = 15.sp
                        )
                    )
                }

                // Status Badge
                when (status) {
                    PermissionDisplayStatus.GRANTED -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SecureSoftGreen)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Check, null, tint = SecureSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Granted",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SecureSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                    PermissionDisplayStatus.REQUIRES_SETUP -> {
                        Button(
                            onClick = onAction,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecureAlert),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(actionLabel, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    PermissionDisplayStatus.UNAVAILABLE -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SecureSurfaceSoft)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Unavailable",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SecureMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    PermissionDisplayStatus.NOT_GRANTED -> {
                        Button(
                            onClick = onAction,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecurePrimary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(actionLabel, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SecureMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
fun VaultPinSetupScreen(
    isConfirming: Boolean,
    onPinEntered: (String) -> Unit,
    onSkip: (() -> Unit)? = null
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isConfirming && onSkip != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("btn_skip_pin")
                ) {
                    Text(
                        text = "Skip for Now",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecurePrimary
                        )
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(SecureSurfaceSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isConfirming) Icons.Outlined.CheckCircle else Icons.Outlined.Lock,
                contentDescription = null,
                tint = SecurePrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isConfirming) "Confirm your PIN" else "Protect your vault",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SecureInk,
                fontSize = 26.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isConfirming) "Enter your new PIN one more time." else "Create a six-digit PIN to protect private media (optional).",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SecureMuted,
                fontSize = 14.sp
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 6 PIN dots
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                val isFilled = index < pin.length
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) SecurePrimary else ScannerRing3)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Numeric Keypad
        NumericKeypad(
            onNumberClick = { num ->
                if (pin.length < 6) {
                    val updated = pin + num
                    pin = updated
                    if (updated.length == 6) {
                        onPinEntered(updated)
                    }
                }
            },
            onDeleteClick = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PINs are never stored in plaintext.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SecureMuted.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: (() -> Unit)? = null
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("bio", "0", "del")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    when (key) {
                        "bio" -> {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .clickable(enabled = onBiometricClick != null) { onBiometricClick?.invoke() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Fingerprint,
                                    contentDescription = "Biometric",
                                    tint = SecurePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        "del" -> {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .clickable { onDeleteClick() }
                                    .testTag("key_delete"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Delete",
                                    tint = SecurePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(SecureSurfaceSoft)
                                    .clickable { onNumberClick(key) }
                                    .testTag("key_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SecureInk,
                                        fontSize = 24.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetupCompleteScreen(
    onGoToApp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.4f))

        ScannerConcentricRings(
            centerIcon = Icons.Outlined.Check,
            isScanning = false,
            size = 260.dp
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "You’re All Set!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SecureInk,
                fontSize = 28.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your private security tools are ready to use.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SecureMuted,
                fontSize = 14.sp
            )
        )

        Spacer(modifier = Modifier.weight(0.6f))

        PrimaryGradientButton(
            text = "Go to App",
            onClick = onGoToApp,
            testTag = "btn_go_to_app"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
