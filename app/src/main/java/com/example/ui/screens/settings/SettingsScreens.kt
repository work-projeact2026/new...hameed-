package com.example.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.SecureLensApp
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    onScanHistoryClick: () -> Unit,
    onPrivacySecurityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onStorageClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val settingsStore = SecureLensApp.instance.settingsStore
    val isDark by settingsStore.isDarkMode.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(title = "Settings")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = SecureMuted,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SecureSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingsRow(
                            title = "Dark Theme",
                            icon = Icons.Outlined.DarkMode,
                            trailing = {
                                Switch(
                                    checked = isDark,
                                    onCheckedChange = { checked ->
                                        coroutineScope.launch {
                                            settingsStore.setDarkMode(checked)
                                        }
                                    }
                                )
                            }
                        )
                        HorizontalDivider(color = SecureDivider)
                        SettingsRow(
                            title = "Language",
                            icon = Icons.Outlined.Translate,
                            onClick = onLanguageClick
                        )
                        HorizontalDivider(color = SecureDivider)
                        SettingsRow(
                            title = "Notifications & Alerts",
                            icon = Icons.Outlined.Notifications,
                            onClick = onNotificationsClick
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Security & Storage",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = SecureMuted,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SecureSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingsRow(
                            title = "Scan History",
                            icon = Icons.Outlined.History,
                            onClick = onScanHistoryClick
                        )
                        HorizontalDivider(color = SecureDivider)
                        SettingsRow(
                            title = "Vault & Privacy Security",
                            icon = Icons.Outlined.Security,
                            onClick = onPrivacySecurityClick
                        )
                        HorizontalDivider(color = SecureDivider)
                        SettingsRow(
                            title = "Storage Management",
                            icon = Icons.Outlined.Storage,
                            onClick = onStorageClick
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Support & Information",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = SecureMuted,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SecureSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        SettingsRow(
                            title = "Help & Guide",
                            icon = Icons.Outlined.HelpOutline,
                            onClick = onHelpClick
                        )
                        HorizontalDivider(color = SecureDivider)
                        SettingsRow(
                            title = "About SecureLens",
                            icon = Icons.Outlined.Info,
                            onClick = onAboutClick
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SecurePrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SecurePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = SecureInk
            ),
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = SecureMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ScanHistoryScreen(
    onBackClick: () -> Unit,
    onStartScan: () -> Unit
) {
    val scanRepo = SecureLensApp.instance.scanRepository
    val sessions by scanRepo.getAllSessions().collectAsState(initial = emptyList())
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = "Scan History",
            onBackClick = onBackClick
        )

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = SecureMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Previous Scans",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecureInk
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Start a Wi-Fi, Bluetooth or Magnetic scan to log security history.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onStartScan,
                        colors = ButtonDefaults.buttonColors(containerColor = SecurePrimary)
                    ) {
                        Text("Start Scan Now")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sessions) { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SecureSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${s.method} Scan",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SecureInk
                                    )
                                )
                                Text(
                                    text = dateFormat.format(Date(s.startedAt)),
                                    style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                                )
                            }
                            Text(
                                text = "${s.devicesCount} devices",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (s.suspiciousCount > 0) SecureAlert else SecureSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSettingsScreen(onBackClick: () -> Unit) {
    val settingsStore = SecureLensApp.instance.settingsStore
    val alertsEnabled by settingsStore.securityAlerts.collectAsState(initial = true)
    val scanNotif by settingsStore.scanNotifications.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(title = "Notification Settings", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecureSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    SettingsRow(
                        title = "Security & Intruder Alerts",
                        icon = Icons.Outlined.NotificationsActive,
                        trailing = {
                            Switch(
                                checked = alertsEnabled,
                                onCheckedChange = { coroutineScope.launch { settingsStore.setSecurityAlerts(it) } }
                            )
                        }
                    )
                    HorizontalDivider(color = SecureDivider)
                    SettingsRow(
                        title = "Scan Summary Notifications",
                        icon = Icons.Outlined.Assessment,
                        trailing = {
                            Switch(
                                checked = scanNotif,
                                onCheckedChange = { coroutineScope.launch { settingsStore.setScanNotifications(it) } }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StorageManagementScreen(
    onBackClick: () -> Unit,
    onOpenVault: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(title = "Storage Management", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecureSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Encrypted Vault Storage",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecureInk
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All recorded evidence is stored securely in app-private storage. You can view or delete files inside the vault.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryGradientButton(
                        text = "Open Encrypted Vault",
                        onClick = onOpenVault,
                        testTag = "btn_open_vault_storage"
                    )
                }
            }
        }
    }
}

@Composable
fun HelpSupportScreen(onBackClick: () -> Unit) {
    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    val topics = remember {
        listOf(
            HelpTopic(
                id = "camera_detection",
                title = "1. Camera Detection (Optical Lens Finder)",
                icon = Icons.Outlined.CameraAlt,
                summary = "How optical lens detection works and how to find hidden pinhole lenses.",
                instructions = listOf(
                    "Turn off room lights or draw blinds so the area is as dark as possible.",
                    "Open Optical Lens Finder and switch on the reflection strobe flash.",
                    "Slowly sweep suspicious fixtures: smoke detectors, picture frames, mirrors, wall clocks, and outlets.",
                    "Look through the viewfinder for sharp pinpoint glints reflecting off optical camera glass.",
                    "When a suspicious glint is located, tap 'Save Proof to Vault' to capture encrypted evidence."
                )
            ),
            HelpTopic(
                id = "wifi_scan",
                title = "2. Wi-Fi Network Scanner",
                icon = Icons.Outlined.Wifi,
                summary = "How to scan local network subnets and interpret surveillance device findings.",
                instructions = listOf(
                    "Connect your phone to the local Wi-Fi network of the premises.",
                    "Launch the Wi-Fi Scanner to sweep all IP addresses connected to the local subnet (LAN).",
                    "The scanner analyzes hostnames, MAC vendor prefixes, and known streaming ports (RTSP, HTTP 8080, ONVIF).",
                    "Devices classified as 'Needs Review' represent potential wireless IP streaming cameras.",
                    "Review device details to note the IP address and hardware manufacturer for your inspection record."
                )
            ),
            HelpTopic(
                id = "bluetooth_scan",
                title = "3. Bluetooth BLE Scanner",
                icon = Icons.Outlined.Bluetooth,
                summary = "How to discover nearby beacons, audio bugs, and covert transmitters.",
                instructions = listOf(
                    "Ensure Bluetooth is enabled on your phone and start the Bluetooth BLE Scan.",
                    "The scanner detects active RF beacons, covert wireless microphones, and unknown trackers.",
                    "Walk slowly across the room while observing signal strength (measured in dBm).",
                    "A signal strength rising towards -40 dBm indicates you are getting closer to the broadcasting device.",
                    "Log suspected devices to investigate further or add them to your room inventory."
                )
            ),
            HelpTopic(
                id = "magnetic_scan",
                title = "4. Magnetic Field Meter (EMF)",
                icon = Icons.Outlined.Sensors,
                summary = "Sensor readings (µT), warning colors, and audio beep alerts.",
                instructions = listOf(
                    "Hold your phone near walls, mirrors, electrical plates, and suspected electronic housings.",
                    "SecureLens measures magnetic flux in microteslas (µT) using your phone's built-in magnetometer.",
                    "Color Indicators: Green (<45 µT) is normal background. Orange (45–75 µT) indicates caution. Red (>75 µT) signals an active electromagnetic anomaly.",
                    "Audio Alerts: Beep frequency increases in tempo as you approach magnetic coils, speakers, and transformers.",
                    "Keep away from metal zippers or phone cases with magnetic kickstands while scanning."
                )
            ),
            HelpTopic(
                id = "thermal_view",
                title = "5. Thermal View Simulation",
                icon = Icons.Outlined.DeviceThermostat,
                summary = "How to use thermal simulation to locate warm, continuously running electronics.",
                instructions = listOf(
                    "Hidden surveillance cameras generate continuous heat from microprocessors and power components.",
                    "Use Thermal View to simulate localized heat dissipation patterns for training and inspection drills.",
                    "Physically inspect any object that feels unusually warm to the touch (e.g. wall adapters, screws, or vents).",
                    "Combine physical heat checks with the optical lens finder for comprehensive verification."
                )
            ),
            HelpTopic(
                id = "secure_recorder",
                title = "6. Secure Video & Audio Recorder",
                icon = Icons.Outlined.Videocam,
                summary = "Step-by-step video and audio evidence recording instructions.",
                instructions = listOf(
                    "Open Secure Recorder from the Home screen or Quick Actions.",
                    "Select Video Mode for live camera viewfinder recording, or Audio Mode for audio memos.",
                    "Tap the red Record button to start capturing tamper-evident security footage.",
                    "All evidence is captured with precise timestamps, location metadata, and file checksums.",
                    "Recordings are saved directly to encrypted app-private storage, hidden from the public gallery."
                )
            ),
            HelpTopic(
                id = "background_recording",
                title = "7. Background Recording & Notification",
                icon = Icons.Outlined.PlayCircle,
                summary = "How to start, pause, resume, and stop recording from the background.",
                instructions = listOf(
                    "Start a recording session manually inside SecureLens while the app is active.",
                    "You can now minimize the app or lock your screen—an Android Foreground Service keeps capturing.",
                    "Use the persistent notification in your status bar to control the recording at any time:",
                    "• Pause: Temporarily pauses recording without closing the file.",
                    "• Resume: Continues capturing in the same session.",
                    "• Stop: Finalizes, encrypts, and safely stores the recording into your Private Vault.",
                    "Tapping the notification opens the active recording screen or completed vault file."
                )
            ),
            HelpTopic(
                id = "private_vault",
                title = "8. Private Vault Storage",
                icon = Icons.Outlined.Lock,
                summary = "Saving, viewing, playing, and managing encrypted media.",
                instructions = listOf(
                    "Open the Private Vault and authenticate using your 6-digit PIN or device biometrics.",
                    "All videos, audio memos, and optical photos are protected with hardware-backed AES-256 encryption.",
                    "Tap any media thumbnail to play video, listen to audio recordings, or review capture metadata.",
                    "Export files or permanently delete evidence with a single tap.",
                    "Vault contents are completely isolated and inaccessible to other third-party phone apps."
                )
            ),
            HelpTopic(
                id = "intruder_guard",
                title = "9. Intruder Guard & Break-in Detection",
                icon = Icons.Outlined.Shield,
                summary = "Setup, failed-unlock threshold, and security-event history.",
                instructions = listOf(
                    "Open Intruder Guard and activate the Android Device Administrator profile when prompted.",
                    "Set your failed-unlock threshold (e.g., 1, 2, or 3 incorrect passcode attempts).",
                    "When an unauthorized person enters the wrong passcode, the exact timestamp is recorded.",
                    "A silent photograph is taken and logged to the security event history where Android allows camera access.",
                    "Open Intruder History at any time to review unauthorized attempts and intruder snapshots."
                )
            ),
            HelpTopic(
                id = "permissions_privacy",
                title = "10. Required Permissions & Privacy",
                icon = Icons.Outlined.Key,
                summary = "How to grant, verify, or update necessary device permissions.",
                instructions = listOf(
                    "Camera: Required for optical lens finder glint sweeps and video recording.",
                    "Microphone: Required for recording audio evidence and voice memos.",
                    "Location & Nearby Devices: Required by Android OS to scan Wi-Fi networks and BLE beacons.",
                    "Notifications: Required for recording status controls (Pause/Resume/Stop) and intruder alerts.",
                    "Device Administrator: Optional, used solely for detecting failed lock-screen passcode attempts.",
                    "To change permissions anytime: Open Android Settings > Apps > SecureLens > Permissions."
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(title = "Help & Support", onBackClick = onBackClick)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Guides & Security Topics",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                Text(
                    text = "Tap any topic below to expand comprehensive step-by-step instructions.",
                    style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(topics) { topic ->
                val isExpanded = expandedTopicId == topic.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedTopicId = if (isExpanded) null else topic.id
                        }
                        .testTag("help_topic_${topic.id}"),
                    colors = CardDefaults.cardColors(containerColor = SecureSurface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SecurePrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = topic.icon,
                                    contentDescription = null,
                                    tint = SecurePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SecureInk
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = topic.summary,
                                    style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = SecureMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (isExpanded) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = SecureDivider
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                topic.instructions.forEachIndexed { index, step ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "•",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = SecurePrimary,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = SecureInk,
                                                lineHeight = 20.sp
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private data class HelpTopic(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val summary: String,
    val instructions: List<String>
)

@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val versionName = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (_: Exception) {
            "1.0.0"
        }
    }

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(title = "About SecureLens", onBackClick = onBackClick)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(SecurePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_securelens_logo),
                        contentDescription = "SecureLens Logo",
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            item {
                Text(
                    text = "SecureLens",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Version $versionName",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecureMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SecureSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "A privacy and hidden electronics scanner designed to help individuals inspect surroundings, safeguard private rooms, and encrypt security evidence.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SecureInk,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SecureSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrivacyDialog = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Policy,
                                contentDescription = null,
                                tint = SecurePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Privacy Policy",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = SecureInk
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SecureMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        HorizontalDivider(color = SecureDivider)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTermsDialog = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Gavel,
                                contentDescription = null,
                                tint = SecurePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Terms of Service",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = SecureInk
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SecureMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "© 2026 SecureLens. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecureMuted,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk
                    )
                )
            },
            text = {
                Text(
                    text = "SecureLens is built with privacy-by-design principles:\n\n" +
                            "• 100% Local Processing: All camera viewfinders, Wi-Fi subnet sweeps, Bluetooth discovery, and magnetic sensor measurements run entirely on your device.\n\n" +
                            "• Zero Cloud Telemetry: SecureLens does not collect, transmit, or sell your personal data, audio, video recordings, or location logs.\n\n" +
                            "• Hardware Encryption: Media saved in your Private Vault is protected using AES-256 encryption backed by Android Keystore.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecureInk,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = SecurePrimary)
                }
            },
            containerColor = SecureSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = "Terms of Service",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk
                    )
                )
            },
            text = {
                Text(
                    text = "SecureLens is provided as a personal security inspection and privacy tool.\n\n" +
                            "• Legitimate Use: The detection tools are intended for inspecting your private premises, hotel rooms, rentals, and personal belongings.\n\n" +
                            "• Compliance: Users are solely responsible for adhering to applicable privacy laws and recording regulations in their jurisdiction.\n\n" +
                            "• Warranty: SecureLens provides detection tools on an 'as-is' basis to assist with manual inspections.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecureInk,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close", color = SecurePrimary)
                }
            },
            containerColor = SecureSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
