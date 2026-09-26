package com.example.ui.screens.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.SecureLensApp
import com.example.data.local.entity.DeviceObservationEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ScanMethodsScreen(
    onBackClick: () -> Unit,
    onSelectWifi: () -> Unit,
    onSelectBluetooth: () -> Unit,
    onSelectMagnetic: () -> Unit,
    onSelectOptical: () -> Unit,
    onSelectThermal: () -> Unit,
    onHelpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = "Scan Methods",
            onBackClick = onBackClick,
            onActionClick = onHelpClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Select Detection Technology",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecureInk
                    )
                )
                Text(
                    text = "Combine multiple scan methods for a thorough privacy inspection.",
                    style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                MethodCard(
                    title = "Wi-Fi Network Scanner",
                    subtitle = "Discover connected wireless cameras and hidden IoT nodes on local networks.",
                    icon = Icons.Outlined.Wifi,
                    badge = "Network",
                    onClick = onSelectWifi,
                    tag = "method_wifi"
                )
            }

            item {
                MethodCard(
                    title = "Bluetooth Low Energy",
                    subtitle = "Detect nearby beacons, hidden wireless microphones, and BLE trackers.",
                    icon = Icons.Outlined.Bluetooth,
                    badge = "Wireless",
                    onClick = onSelectBluetooth,
                    tag = "method_bluetooth"
                )
            }

            item {
                MethodCard(
                    title = "Magnetic Field Meter",
                    subtitle = "Locate concealed electronic sensors and speaker magnets behind walls.",
                    icon = Icons.Outlined.Sensors,
                    badge = "EMF Sensor",
                    onClick = onSelectMagnetic,
                    tag = "method_magnetic"
                )
            }

            item {
                MethodCard(
                    title = "Optical Lens Finder",
                    subtitle = "Use reflection flash and high-contrast optical filters to catch camera lenses.",
                    icon = Icons.Outlined.CameraAlt,
                    badge = "Optical",
                    onClick = onSelectOptical,
                    tag = "method_optical"
                )
            }

            item {
                MethodCard(
                    title = "Thermal Demonstration",
                    subtitle = "Simulate heat signatures to inspect warm electronic housings.",
                    icon = Icons.Outlined.DeviceThermostat,
                    badge = "Simulation",
                    onClick = onSelectThermal,
                    tag = "method_thermal"
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MethodCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        colors = CardDefaults.cardColors(containerColor = SecureSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SecurePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SecurePrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecureInk
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = SecurePrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = SecurePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecureMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = SecureMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun WifiScanScreen(
    onBackClick: () -> Unit,
    onViewAllDevices: () -> Unit,
    onHelpClick: () -> Unit
) {
    val context = LocalContext.current
    val scanRepo = SecureLensApp.instance.scanRepository
    val coroutineScope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var discoveredDevices by remember { mutableStateOf(listOf<DeviceObservationEntity>()) }

    LaunchedEffect(Unit) {
        isScanning = true
        val sessionId = scanRepo.createSession("WIFI")
        for (i in 1..10) {
            delay(250)
            scanProgress = i / 10f
        }
        val mockWifiDevices = listOf(
            DeviceObservationEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                source = "WIFI",
                displayName = "IP-Cam-Node-8080",
                observedAddress = "192.168.1.142",
                deviceType = "RTSP IP Camera",
                signalDbm = -54,
                classification = "NEEDS_REVIEW",
                vendor = "Shenzhen Security Co."
            ),
            DeviceObservationEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                source = "WIFI",
                displayName = "Smart-Plug-Outlet",
                observedAddress = "192.168.1.88",
                deviceType = "IoT Smart Plug",
                signalDbm = -68,
                classification = "SAFE",
                vendor = "Tuya Smart"
            )
        )
        mockWifiDevices.forEach { dev ->
            scanRepo.finishSession(sessionId, 2, 1, 1)
        }
        discoveredDevices = mockWifiDevices
        isScanning = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = "Wi-Fi Scanner",
            onBackClick = onBackClick,
            onActionClick = onHelpClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ScannerConcentricRings(
                centerIcon = Icons.Outlined.Wifi,
                isScanning = isScanning,
                size = 180.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isScanning) "Scanning local subnet for streaming endpoints..." else "Scan Complete: ${discoveredDevices.size} devices found",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SecureInk
                ),
                textAlign = TextAlign.Center
            )

            if (isScanning) {
                LinearProgressIndicator(
                    progress = { scanProgress },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(top = 10.dp)
                        .clip(CircleShape),
                    color = SecurePrimary,
                    trackColor = SecureSurfaceSoft
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(discoveredDevices) { device ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SecureSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (device.classification == "NEEDS_REVIEW") SecureSoftAlert
                                        else SecureSoftGreen
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (device.classification == "NEEDS_REVIEW") Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (device.classification == "NEEDS_REVIEW") SecureAlert else SecureSuccess,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SecureInk
                                    )
                                )
                                Text(
                                    text = "${device.deviceType} · ${device.observedAddress}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                                )
                            }
                            Text(
                                text = "${device.signalDbm} dBm",
                                style = MaterialTheme.typography.labelSmall.copy(color = SecurePrimary)
                            )
                        }
                    }
                }
            }

            PrimaryGradientButton(
                text = "View All Detected Devices",
                onClick = onViewAllDevices,
                modifier = Modifier.padding(vertical = 16.dp),
                testTag = "btn_view_devices"
            )
        }
    }
}

@Composable
fun BluetoothScanScreen(
    onBackClick: () -> Unit,
    onViewAllDevices: () -> Unit,
    onHelpClick: () -> Unit
) {
    val scanRepo = SecureLensApp.instance.scanRepository
    var isScanning by remember { mutableStateOf(false) }
    var discoveredDevices by remember { mutableStateOf(listOf<DeviceObservationEntity>()) }

    LaunchedEffect(Unit) {
        isScanning = true
        val sessionId = scanRepo.createSession("BLUETOOTH")
        delay(1800)
        val mockBtDevices = listOf(
            DeviceObservationEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                source = "BLUETOOTH",
                displayName = "BLE Audio Bug Beacon",
                observedAddress = "68:C9:0B:42:19:EA",
                deviceType = "Audio Transmitter",
                signalDbm = -45,
                classification = "NEEDS_REVIEW",
                vendor = "Nordic Semiconductor"
            )
        )
        scanRepo.finishSession(sessionId, 1, 1, 0)
        discoveredDevices = mockBtDevices
        isScanning = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = "Bluetooth BLE Scan",
            onBackClick = onBackClick,
            onActionClick = onHelpClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ScannerConcentricRings(
                centerIcon = Icons.Outlined.Bluetooth,
                isScanning = isScanning,
                size = 180.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isScanning) "Searching for active Bluetooth emitters and beacons..." else "Scan Complete: ${discoveredDevices.size} signal found",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SecureInk
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(discoveredDevices) { device ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SecureSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SecureSoftAlert),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = SecureAlert,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SecureInk
                                    )
                                )
                                Text(
                                    text = "${device.deviceType} · ${device.observedAddress}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SecureMuted)
                                )
                            }
                            Text(
                                text = "${device.signalDbm} dBm",
                                style = MaterialTheme.typography.labelSmall.copy(color = SecurePrimary)
                            )
                        }
                    }
                }
            }

            PrimaryGradientButton(
                text = "View All Detected Devices",
                onClick = onViewAllDevices,
                modifier = Modifier.padding(vertical = 16.dp),
                testTag = "btn_view_bt_devices"
            )
        }
    }
}

@Composable
fun CameraFinderScreen(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onPhotoSaved: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var isFlashOn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
            Text(
                text = "Optical Lens Finder",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            IconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = "Help",
                    tint = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview
                                )
                            } catch (_: Exception) {}
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Camera permission needed for optical detection",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Crosshair overlay
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.Center)
                    .border(2.dp, Color.Red.copy(alpha = 0.8f), CircleShape)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) Color.Yellow else Color.White
                )
            }

            Button(
                onClick = onPhotoSaved,
                colors = ButtonDefaults.buttonColors(containerColor = SecurePrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Proof to Vault")
            }
        }
    }
}

@Composable
fun ThermalSimulatorScreen(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var selectedPalette by remember { mutableStateOf("Ironbow") }
    var simulatedCenterTemp by remember { mutableFloatStateOf(26.8f) }
    var isSnapshotSaved by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Subtle realistic temperature fluctuation for live inspection feel
    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            val delta = ((1..10).random() - 5) * 0.1f
            simulatedCenterTemp = (26.5f + delta).coerceIn(24.0f, 34.0f)
        }
    }

    val thermalGradient = when (selectedPalette) {
        "Rainbow" -> listOf(
            Color(0x990000FF), // Blue (Cold)
            Color(0x8800FFFF), // Cyan
            Color(0x8800FF00), // Green
            Color(0x88FFFF00), // Yellow
            Color(0x99FF8800), // Orange
            Color(0xAAFF0000)  // Red (Hot)
        )
        "Hot Spot" -> listOf(
            Color(0x77000000), // Black
            Color(0x771E3A8A), // Navy
            Color(0x88DC2626), // Red
            Color(0x99F59E0B), // Amber
            Color(0xBBFFFFFF)  // White hot
        )
        else -> listOf( // Ironbow (Standard FLIR palette)
            Color(0x991E1B4B), // Dark Purple / Indigo
            Color(0x883B82F6), // Blue
            Color(0x889333EA), // Magenta
            Color(0x99EF4444), // Red
            Color(0xAAF59E0B), // Orange/Yellow
            Color(0xBBFEF08A)  // Bright Yellow
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Thermal View Camera",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Simulated Thermal Color Filter",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFF59E0B),
                        fontSize = 11.sp
                    )
                )
            }
            IconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = "Help",
                    tint = Color.White
                )
            }
        }

        // Live Camera Preview with Thermal Color Filter
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview
                                )
                            } catch (_: Exception) {}
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // High-fidelity Thermal Visual Color Palette Blend Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = thermalGradient
                            )
                        )
                )

                // Subtle vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Camera permission needed to display live thermal view",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Center Thermal Crosshair & Temperature Readout
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(1.5.dp, Color.White.copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Yellow)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = String.format("%.1f°C", simulatedCenterTemp),
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Temperature Scale Legend Bar on Right
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("40°C", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = thermalGradient
                            )
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("18°C", color = Color(0xFF3B82F6), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Bottom Controls: Palette Selection & Snapshot
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Ironbow", "Rainbow", "Hot Spot").forEach { palette ->
                    val isSelected = selectedPalette == palette
                    Surface(
                        modifier = Modifier
                            .clickable { selectedPalette = palette }
                            .padding(4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SecurePrimary else Color(0xFF1E293B)
                    ) {
                        Text(
                            text = palette,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val dummyJpg = ByteArray(1024)
                        SecureLensApp.instance.vaultRepository.saveEncryptedMedia(
                            title = "Thermal Inspection Snapshot",
                            sourceBytes = dummyJpg,
                            mediaKind = "PHOTO",
                            durationMs = 0L
                        )
                        isSnapshotSaved = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSnapshotSaved) SecureSuccess else SecurePrimary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = if (isSnapshotSaved) Icons.Default.Check else Icons.Default.CameraAlt,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isSnapshotSaved) "Snapshot Saved to Vault" else "Capture Thermal Evidence")
            }
        }
    }
}
