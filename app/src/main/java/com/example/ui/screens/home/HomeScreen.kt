package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onStartFullScan: () -> Unit,
    onScanWifi: () -> Unit,
    onScanBluetooth: () -> Unit,
    onCameraDetection: () -> Unit,
    onTipsGuide: () -> Unit,
    onMagneticScan: () -> Unit,
    onThermalView: () -> Unit,
    onSecureRecorder: () -> Unit,
    onPrivateVault: () -> Unit,
    onIntruderGuard: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val isDark = LocalSecureColors.current.isDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk,
                        fontSize = 26.sp,
                        lineHeight = 32.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecureMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
            }

            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SecureSurfaceSoft)
                    .testTag("btn_notifications")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notifications),
                    tint = SecureInk,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Hero Card (Start Full Scan)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = SecurePrimary.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(HeroCardGradient)
                .clickable { onStartFullScan() }
                .testTag("hero_full_scan")
        ) {
            FullScanHeroIllustration(modifier = Modifier.fillMaxSize())

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.start_full_scan),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.start_full_scan_desc),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start",
                        tint = SecurePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row 1: Wi-Fi + Bluetooth
        Row(modifier = Modifier.fillMaxWidth()) {
            IllustratedHomeCard(
                title = stringResource(R.string.scan_wifi),
                description = stringResource(R.string.scan_wifi_desc),
                icon = Icons.Outlined.Wifi,
                gradientColors = if (isDark) listOf(Color(0xFF132238), Color(0xFF0F1B2D)) else listOf(Color(0xFFEAF5FF), Color(0xFFD6ECFF)),
                illustration = { WifiIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onScanWifi,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IllustratedHomeCard(
                title = stringResource(R.string.scan_bluetooth),
                description = stringResource(R.string.scan_bluetooth_desc),
                icon = Icons.Outlined.Bluetooth,
                gradientColors = if (isDark) listOf(Color(0xFF141F38), Color(0xFF10172C)) else listOf(Color(0xFFEFF3FF), Color(0xFFDEE8FF)),
                illustration = { BluetoothIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onScanBluetooth,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Camera Detection + Tips & Guide
        Row(modifier = Modifier.fillMaxWidth()) {
            IllustratedHomeCard(
                title = stringResource(R.string.camera_detection),
                description = stringResource(R.string.camera_detection_desc),
                icon = Icons.Outlined.RemoveRedEye,
                gradientColors = if (isDark) listOf(Color(0xFF2B1920), Color(0xFF1F1218)) else listOf(Color(0xFFFFF2F0), Color(0xFFFFE3E0)),
                illustration = { CameraDetectionIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onCameraDetection,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IllustratedHomeCard(
                title = stringResource(R.string.tips_guide),
                description = stringResource(R.string.tips_guide_desc),
                icon = Icons.Outlined.VerifiedUser,
                gradientColors = if (isDark) listOf(Color(0xFF102A24), Color(0xFF0C1F1B)) else listOf(Color(0xFFEDFAF6), Color(0xFFD6F5ED)),
                illustration = { TipsGuideIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onTipsGuide,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: Magnetic Scanner + Thermal View
        Row(modifier = Modifier.fillMaxWidth()) {
            IllustratedHomeCard(
                title = stringResource(R.string.magnetic_scanner),
                description = stringResource(R.string.magnetic_scanner_desc),
                icon = Icons.Outlined.Explore,
                gradientColors = if (isDark) listOf(Color(0xFF221A3B), Color(0xFF18122B)) else listOf(Color(0xFFF3F0FF), Color(0xFFE6DEFF)),
                illustration = { MagneticIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onMagneticScan,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IllustratedHomeCard(
                title = stringResource(R.string.thermal_view),
                description = stringResource(R.string.thermal_view_desc),
                icon = Icons.Outlined.DeviceThermostat,
                gradientColors = if (isDark) listOf(Color(0xFF332015), Color(0xFF24150D)) else listOf(Color(0xFFFFF7EB), Color(0xFFFFEAD1)),
                illustration = { ThermalIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onThermalView,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 4: Secure Recorder + Private Vault
        Row(modifier = Modifier.fillMaxWidth()) {
            IllustratedHomeCard(
                title = stringResource(R.string.secure_recorder),
                description = stringResource(R.string.secure_recorder_desc),
                icon = Icons.Outlined.Videocam,
                gradientColors = if (isDark) listOf(Color(0xFF331822), Color(0xFF241017)) else listOf(Color(0xFFFFF0F3), Color(0xFFFFDFE6)),
                illustration = { RecorderIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onSecureRecorder,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IllustratedHomeCard(
                title = stringResource(R.string.private_vault),
                description = stringResource(R.string.private_vault_desc),
                icon = Icons.Outlined.Lock,
                gradientColors = if (isDark) listOf(Color(0xFF12233B), Color(0xFF0D1829)) else listOf(Color(0xFFEBF7FF), Color(0xFFD6EFFF)),
                illustration = { VaultIllustration(modifier = Modifier.fillMaxSize()) },
                onClick = onPrivateVault,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Intruder Guard Full Width Card
        val intruderGradient = if (isDark) listOf(Color(0xFF132238), Color(0xFF0F1B2D)) else listOf(Color(0xFFE8F3FF), Color(0xFFD0E7FF))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(106.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onIntruderGuard() }
                .testTag("card_intruder_guard")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(intruderGradient))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (isDark) 0.15f else 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = SecurePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.intruder_guard),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SecureInk,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.intruder_guard_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SecureMuted,
                                fontSize = 12.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dedicated dome camera illustration box - guaranteed never to overlap text!
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .padding(end = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IntruderGuardIllustration(modifier = Modifier.fillMaxSize())
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (isDark) 0.2f else 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SecurePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun IllustratedHomeCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    illustration: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalSecureColors.current.isDark

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
        ) {
            // Dedicated top-right illustration container - completely clears the bottom text area!
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(width = 84.dp, height = 76.dp)
                    .padding(top = 6.dp, end = 6.dp)
            ) {
                illustration()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row with round icon badge on top-left
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (isDark) 0.15f else 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = SecurePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom row with title & subtitle text and circular forward arrow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SecureInk,
                                fontSize = 15.sp,
                                lineHeight = 19.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SecureMuted,
                                fontSize = 11.5.sp,
                                lineHeight = 14.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (isDark) 0.2f else 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SecurePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
