package com.example.ui.screens.scan

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlin.math.sqrt

@Composable
fun MagneticScanScreen(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val context = LocalContext.current
    var isMeasuring by remember { mutableStateOf(true) }
    var magneticMagnitude by remember { mutableFloatStateOf(48f) }
    var isAudioAlertEnabled by remember { mutableStateOf(true) }
    var showCalibrateDialog by remember { mutableStateOf(false) }
    val historyValues = remember {
        mutableStateListOf(42f, 44f, 46f, 45f, 47f, 48f, 49f, 48f, 50f, 47f, 46f, 48f, 51f, 48f)
    }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
    val magneticSensor = remember { sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }

    // Fast ToneGenerator for responsive auditory feedback on strong anomalies
    var toneGenerator by remember { mutableStateOf<ToneGenerator?>(null) }
    var lastBeepTime by remember { mutableLongStateOf(0L) }

    DisposableEffect(isMeasuring) {
        if (isMeasuring) {
            toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            } catch (e: Exception) {
                null
            }

            val listener = object : SensorEventListener {
                private var lastUiUpdateTime = 0L

                override fun onSensorChanged(event: SensorEvent?) {
                    val ev = event ?: return
                    val x = ev.values[0]
                    val y = ev.values[1]
                    val z = ev.values[2]
                    val mag = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                    val now = System.currentTimeMillis()
                    // Responsive sensor calculation with lightweight 30ms throttling for rendering
                    if (now - lastUiUpdateTime >= 30L) {
                        lastUiUpdateTime = now
                        magneticMagnitude = mag

                        if (historyValues.size >= 24) {
                            historyValues.removeAt(0)
                        }
                        historyValues.add(mag)

                        // Trigger alert beep when strong anomaly is detected (>= 85 µT) with 400ms cooldown
                        if (isAudioAlertEnabled && mag >= 85f && (now - lastBeepTime >= 400L)) {
                            lastBeepTime = now
                            try {
                                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
                            } catch (e: Exception) {}
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            // Use SENSOR_DELAY_GAME for fast, real-time responsive updates without delay
            if (magneticSensor != null) {
                sensorManager?.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_GAME)
            }

            onDispose {
                sensorManager?.unregisterListener(listener)
                try {
                    toneGenerator?.stopTone()
                    toneGenerator?.release()
                } catch (e: Exception) {}
                toneGenerator = null
            }
        } else {
            try {
                toneGenerator?.stopTone()
                toneGenerator?.release()
            } catch (e: Exception) {}
            toneGenerator = null
            onDispose {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecureBackground)
    ) {
        AppHeader(
            title = stringResource(R.string.magnetic_title),
            onBackClick = onBackClick,
            onActionClick = onHelpClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic intensity colors: Normal: Blue/Cyan | Elevated: Yellow/Orange | Strong: Red
            val isDarkTheme = LocalSecureColors.current.isDark
            val currentIntensityColor = when {
                !isMeasuring -> SecureInk
                magneticMagnitude < 55f -> Color(0xFF0284C7) // Normal: Blue/Cyan
                magneticMagnitude < 85f -> Color(0xFFF59E0B) // Elevated: Yellow/Orange
                else -> Color(0xFFEF4444) // Strong: Red
            }

            val gaugeBrush = when {
                magneticMagnitude < 55f -> Brush.horizontalGradient(listOf(Color(0xFF0284C7), Color(0xFF06B6D4)))
                magneticMagnitude < 85f -> Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFFBBF24)))
                else -> Brush.horizontalGradient(listOf(Color(0xFFDC2626), Color(0xFFF87171)))
            }

            val trackColor = if (isDarkTheme) Color(0xFF1E2E47) else Color(0xFFE2E8F0)

            // Ultra-responsive gauge animation (60ms tween eliminates lagging spring delay)
            val animatedProgress by animateFloatAsState(
                targetValue = (magneticMagnitude / 120f).coerceIn(0.04f, 1.0f),
                animationSpec = tween(durationMillis = 60, easing = LinearEasing),
                label = "gauge_progress"
            )

            // Audio Alert Toggle & Status Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isMeasuring) currentIntensityColor else SecureMuted)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMeasuring) "Live Sensor Active" else "Sensor Idle",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isMeasuring) currentIntensityColor else SecureMuted,
                            fontSize = 11.5.sp
                        )
                    )
                }

                // Audio warning beep toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAudioAlertEnabled) SecureSurfaceSoft else SecureSurfaceSoft.copy(alpha = 0.5f))
                        .clickable { isAudioAlertEnabled = !isAudioAlertEnabled }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAudioAlertEnabled) Icons.Outlined.VolumeUp else Icons.Outlined.VolumeOff,
                        contentDescription = "Warning Beep",
                        tint = if (isAudioAlertEnabled) SecurePrimary else SecureMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Beep Alert",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isAudioAlertEnabled) SecurePrimary else SecureMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gauge Arc
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Track Arc
                    drawArc(
                        color = trackColor,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 22f, cap = StrokeCap.Round)
                    )
                    // Dynamic Indicator Arc
                    if (isMeasuring) {
                        drawArc(
                            brush = gaugeBrush,
                            startAngle = 135f,
                            sweepAngle = 270f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 22f, cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isMeasuring) "${magneticMagnitude.toInt()}" else "—",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = currentIntensityColor,
                            fontSize = 48.sp
                        )
                    )
                    Text(
                        text = "µT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecureInk,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.magnetic_field),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecureMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val statusText = when {
                !isMeasuring -> stringResource(R.string.ready_to_measure)
                magneticMagnitude < 55f -> stringResource(R.string.safe_baseline)
                magneticMagnitude < 85f -> stringResource(R.string.elevated_field)
                else -> stringResource(R.string.strong_anomaly)
            }

            val statusBg = when {
                !isMeasuring -> if (isDarkTheme) Color(0xFF132338) else Color(0xFFEFF6FF)
                magneticMagnitude < 55f -> if (isDarkTheme) Color(0xFF0C243B) else Color(0xFFE0F2FE)
                magneticMagnitude < 85f -> if (isDarkTheme) Color(0xFF78350F) else Color(0xFFFEF3C7)
                else -> if (isDarkTheme) Color(0xFF7F1D1D) else Color(0xFFFEE2E2)
            }

            val statusTextColor = when {
                !isMeasuring -> if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
                magneticMagnitude < 55f -> if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF0369A1)
                magneticMagnitude < 85f -> if (isDarkTheme) Color(0xFFFCD34D) else Color(0xFF92400E)
                else -> if (isDarkTheme) Color(0xFFFCA5A5) else Color(0xFF991B1B)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusBg)
                    .border(width = 1.dp, color = statusTextColor.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Waveform / trend card with rolling buffer
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SecureSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.magnetic_field),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SecureInk
                            )
                        )
                        Text(
                            text = "Live trend (50 Hz)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SecurePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic bar chart with color-response
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        historyValues.takeLast(20).forEach { value ->
                            val heightFraction = (value / 100f).coerceIn(0.12f, 1f)
                            val barColor = when {
                                value < 55f -> Color(0xFF0284C7)
                                value < 85f -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }
                            Box(
                                modifier = Modifier
                                    .width(7.dp)
                                    .fillMaxHeight(heightFraction)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(barColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Direct magnetometer sensor telemetry · Fast response rate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecureMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SecureSurfaceSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.magnetic_disclaimer),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SecureMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isMeasuring) {
                PrimaryGradientButton(
                    text = stringResource(R.string.start_magnetic_scan),
                    onClick = { isMeasuring = true },
                    testTag = "btn_start_magnetic"
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryPillButton(
                    text = stringResource(R.string.calibrate_guide),
                    onClick = { showCalibrateDialog = true },
                    testTag = "btn_calibrate"
                )
            } else {
                SecondaryPillButton(
                    text = stringResource(R.string.stop_scan),
                    leadingIcon = Icons.Filled.Stop,
                    onClick = { isMeasuring = false },
                    testTag = "btn_stop_magnetic"
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryPillButton(
                    text = "Calibrate Baseline",
                    leadingIcon = Icons.Outlined.Sync,
                    onClick = { showCalibrateDialog = true },
                    testTag = "btn_calibrate_active"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showCalibrateDialog) {
            AlertDialog(
                onDismissRequest = { showCalibrateDialog = false },
                containerColor = SecureSurface,
                title = {
                    Text(
                        text = "Calibration Guide",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SecureInk
                        )
                    )
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SecureSurfaceSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Sync,
                                contentDescription = null,
                                tint = SecurePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Move in a figure eight",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SecureInk
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Move your phone gently through the air in an 8 pattern away from large metal objects, then compare readings.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SecureMuted,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCalibrateDialog = false }) {
                        Text(stringResource(R.string.understood), color = SecurePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
