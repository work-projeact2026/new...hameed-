package com.example.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalSecureColors

@Composable
fun FullScanHeroIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.85f, h * 0.5f)

        // Concentric radar scan rings
        val rings = listOf(w * 0.15f, w * 0.32f, w * 0.48f, w * 0.65f)
        rings.forEachIndexed { index, radius ->
            drawCircle(
                color = Color.White.copy(alpha = 0.08f + index * 0.03f),
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Radar sweep gradient wedge
        val sweepBrush = Brush.sweepGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            ),
            center = center
        )
        drawCircle(
            brush = sweepBrush,
            radius = rings.last(),
            center = center
        )
    }
}

@Composable
fun WifiIllustration(modifier: Modifier = Modifier) {
    val isDark = LocalSecureColors.current.isDark
    val primaryColor = Color(0xFF127CF5)
    val secondaryColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)

    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.65f

        // Center dot
        drawCircle(
            color = primaryColor,
            radius = 4.5.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Concentric 3D Wi-Fi waves scaled to fit top-right box
        val arcs = listOf(
            Pair(14.dp.toPx(), 4.5.dp.toPx()),
            Pair(26.dp.toPx(), 5.5.dp.toPx()),
            Pair(38.dp.toPx(), 6.5.dp.toPx())
        )

        arcs.forEachIndexed { idx, (radius, strokeW) ->
            val alpha = 0.75f + (idx * 0.12f)
            drawArc(
                brush = Brush.horizontalGradient(
                    listOf(secondaryColor.copy(alpha = alpha), primaryColor.copy(alpha = alpha))
                ),
                startAngle = 205f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun BluetoothIllustration(modifier: Modifier = Modifier) {
    val isDark = LocalSecureColors.current.isDark
    val btColor = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
    val glowColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF93C5FD)

    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val unit = 10.dp.toPx()

        // Background glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor.copy(alpha = 0.35f), Color.Transparent),
                center = Offset(cx, cy),
                radius = 32.dp.toPx()
            ),
            radius = 32.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Bluetooth rune path
        val path = Path().apply {
            moveTo(cx, cy - unit * 2f)
            lineTo(cx, cy + unit * 2f)

            lineTo(cx + unit * 1.1f, cy + unit * 0.9f)
            lineTo(cx - unit * 0.9f, cy - unit * 0.9f)

            moveTo(cx - unit * 0.9f, cy + unit * 0.9f)
            lineTo(cx + unit * 1.1f, cy - unit * 0.9f)
            lineTo(cx, cy - unit * 2f)
        }

        drawPath(
            path = path,
            color = btColor,
            style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Accent satellite dots
        drawCircle(color = btColor.copy(alpha = 0.6f), radius = 2.5.dp.toPx(), center = Offset(cx - 18.dp.toPx(), cy - 10.dp.toPx()))
        drawCircle(color = btColor.copy(alpha = 0.6f), radius = 3.dp.toPx(), center = Offset(cx + 20.dp.toPx(), cy + 12.dp.toPx()))
    }
}

@Composable
fun CameraDetectionIllustration(modifier: Modifier = Modifier) {
    val isDark = LocalSecureColors.current.isDark

    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f

        // Framing corner reticles
        val cornerColor = if (isDark) Color(0xFFF87171).copy(alpha = 0.7f) else Color(0xFFEF4444).copy(alpha = 0.6f)
        val boxHalf = 27.dp.toPx()
        val cornerLen = 9.dp.toPx()
        val stroke = 2.5.dp.toPx()

        // Top-left
        drawLine(cornerColor, Offset(cx - boxHalf, cy - boxHalf), Offset(cx - boxHalf + cornerLen, cy - boxHalf), stroke)
        drawLine(cornerColor, Offset(cx - boxHalf, cy - boxHalf), Offset(cx - boxHalf, cy - boxHalf + cornerLen), stroke)
        // Top-right
        drawLine(cornerColor, Offset(cx + boxHalf, cy - boxHalf), Offset(cx + boxHalf - cornerLen, cy - boxHalf), stroke)
        drawLine(cornerColor, Offset(cx + boxHalf, cy - boxHalf), Offset(cx + boxHalf, cy - boxHalf + cornerLen), stroke)
        // Bottom-left
        drawLine(cornerColor, Offset(cx - boxHalf, cy + boxHalf), Offset(cx - boxHalf + cornerLen, cy + boxHalf), stroke)
        drawLine(cornerColor, Offset(cx - boxHalf, cy + boxHalf), Offset(cx - boxHalf, cy + boxHalf - cornerLen), stroke)
        // Bottom-right
        drawLine(cornerColor, Offset(cx + boxHalf, cy + boxHalf), Offset(cx + boxHalf - cornerLen, cy + boxHalf), stroke)
        drawLine(cornerColor, Offset(cx + boxHalf, cy + boxHalf), Offset(cx + boxHalf, cy + boxHalf - cornerLen), stroke)

        // Outer lens barrel
        drawCircle(
            color = Color(0xFF1E293B),
            radius = 20.dp.toPx(),
            center = Offset(cx, cy)
        )
        // Metallic aperture ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF38BDF8), Color(0xFF0F172A)),
                center = Offset(cx, cy),
                radius = 17.dp.toPx()
            ),
            radius = 17.dp.toPx(),
            center = Offset(cx, cy)
        )
        // Center aperture pupil
        drawCircle(
            color = Color(0xFF020617),
            radius = 10.dp.toPx(),
            center = Offset(cx, cy)
        )
        // Specular glint
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = 3.dp.toPx(),
            center = Offset(cx - 3.5.dp.toPx(), cy - 3.5.dp.toPx())
        )
    }
}

@Composable
fun TipsGuideIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.56f

        val bookColor = Color(0xFF38BDF8)
        val pageColor = Color.White
        val bookW = 30.dp.toPx()
        val bookH = 20.dp.toPx()

        // Left page
        drawRoundRect(
            color = bookColor,
            topLeft = Offset(cx - bookW, cy - bookH * 0.5f),
            size = Size(bookW * 0.95f, bookH),
            cornerRadius = CornerRadius(3.dp.toPx())
        )
        drawRoundRect(
            color = pageColor.copy(alpha = 0.9f),
            topLeft = Offset(cx - bookW + 2.dp.toPx(), cy - bookH * 0.5f + 2.dp.toPx()),
            size = Size(bookW * 0.95f - 4.dp.toPx(), bookH - 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        // Right page
        drawRoundRect(
            color = bookColor,
            topLeft = Offset(cx + 2.dp.toPx(), cy - bookH * 0.5f),
            size = Size(bookW * 0.95f, bookH),
            cornerRadius = CornerRadius(3.dp.toPx())
        )
        drawRoundRect(
            color = pageColor.copy(alpha = 0.9f),
            topLeft = Offset(cx + 4.dp.toPx(), cy - bookH * 0.5f + 2.dp.toPx()),
            size = Size(bookW * 0.95f - 4.dp.toPx(), bookH - 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        // Glowing lightbulb on top
        val bulbCenter = Offset(cx + bookW * 0.3f, cy - bookH * 0.8f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B)),
                center = bulbCenter,
                radius = 9.dp.toPx()
            ),
            radius = 8.dp.toPx(),
            center = bulbCenter
        )
        drawRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(bulbCenter.x - 2.5.dp.toPx(), bulbCenter.y + 6.dp.toPx()),
            size = Size(5.dp.toPx(), 3.5.dp.toPx())
        )
    }
}

@Composable
fun MagneticIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.48f

        // Magnetic field curved flux lines
        val fluxColor = Color(0xFFA78BFA).copy(alpha = 0.4f)
        val arcs = listOf(24.dp.toPx(), 33.dp.toPx())
        arcs.forEach { r ->
            drawArc(
                color = fluxColor,
                startAngle = 160f,
                sweepAngle = 220f,
                useCenter = false,
                topLeft = Offset(cx - r, cy - r),
                size = Size(r * 2, r * 2),
                style = Stroke(width = 1.2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)))
            )
        }

        // Horseshoe magnet
        val magnetR = 17.dp.toPx()
        val magnetW = 7.dp.toPx()

        // Red arm (North)
        val redPath = Path().apply {
            moveTo(cx - magnetR, cy + 10.dp.toPx())
            lineTo(cx - magnetR, cy)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(cx - magnetR, cy - magnetR, cx + magnetR, cy + magnetR),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
        }
        drawPath(
            path = redPath,
            color = Color(0xFFEF4444),
            style = Stroke(width = magnetW, cap = StrokeCap.Butt)
        )

        // Blue arm (South)
        val bluePath = Path().apply {
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(cx - magnetR, cy - magnetR, cx + magnetR, cy + magnetR),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = true
            )
            lineTo(cx + magnetR, cy + 10.dp.toPx())
        }
        drawPath(
            path = bluePath,
            color = Color(0xFF3B82F6),
            style = Stroke(width = magnetW, cap = StrokeCap.Butt)
        )

        // Silver tips
        drawRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(cx - magnetR - magnetW / 2, cy + 10.dp.toPx()),
            size = Size(magnetW, 6.dp.toPx())
        )
        drawRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(cx + magnetR - magnetW / 2, cy + 10.dp.toPx()),
            size = Size(magnetW, 6.dp.toPx())
        )
    }
}

@Composable
fun ThermalIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f

        // Multi-ring thermal heatmap blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFEF4444).copy(alpha = 0.9f),
                    Color(0xFFF97316).copy(alpha = 0.75f),
                    Color(0xFFFBBF24).copy(alpha = 0.5f),
                    Color(0xFF38BDF8).copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = 24.dp.toPx()
            ),
            radius = 24.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Crosshair reticle
        val reticleColor = Color.White.copy(alpha = 0.9f)
        val crossLen = 11.dp.toPx()
        drawLine(reticleColor, Offset(cx - crossLen, cy), Offset(cx + crossLen, cy), 1.5.dp.toPx())
        drawLine(reticleColor, Offset(cx, cy - crossLen), Offset(cx, cy + crossLen), 1.5.dp.toPx())
        drawCircle(reticleColor, radius = 6.dp.toPx(), center = Offset(cx, cy), style = Stroke(1.5.dp.toPx()))
    }
}

@Composable
fun RecorderIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f

        // Flashing red recording indicator dot at top-right
        drawCircle(
            color = Color(0xFFEF4444),
            radius = 4.dp.toPx(),
            center = Offset(cx + 14.dp.toPx(), cy - 16.dp.toPx())
        )

        // Microphone body
        val micW = 10.dp.toPx()
        val micH = 19.dp.toPx()
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF0F172A))),
            topLeft = Offset(cx - micW * 0.5f, cy - micH * 0.6f),
            size = Size(micW, micH),
            cornerRadius = CornerRadius(micW * 0.5f)
        )

        // Microphone stand cradle arc
        val cradleR = 11.dp.toPx()
        drawArc(
            color = Color(0xFF64748B),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - cradleR, cy - cradleR * 0.2f),
            size = Size(cradleR * 2, cradleR * 2),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
        // Stand line
        drawLine(
            Color(0xFF64748B),
            Offset(cx, cy + cradleR * 1.5f),
            Offset(cx, cy + cradleR * 2.0f),
            strokeWidth = 2.5.dp.toPx()
        )

        // Audio waveform lines left and right
        val waveColor = Color(0xFFF43F5E).copy(alpha = 0.45f)
        val waveH = listOf(6.dp.toPx(), 12.dp.toPx(), 18.dp.toPx())
        waveH.forEachIndexed { i, hVal ->
            val offsetLeft = (i + 1) * 6.dp.toPx()
            drawLine(waveColor, Offset(cx - cradleR - offsetLeft, cy - hVal * 0.5f), Offset(cx - cradleR - offsetLeft, cy + hVal * 0.5f), 2.dp.toPx(), StrokeCap.Round)
            drawLine(waveColor, Offset(cx + cradleR + offsetLeft, cy - hVal * 0.5f), Offset(cx + cradleR + offsetLeft, cy + hVal * 0.5f), 2.dp.toPx(), StrokeCap.Round)
        }
    }
}

@Composable
fun VaultIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f

        // 3D metallic blue safe box
        val boxSize = 34.dp.toPx()
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF60A5FA), Color(0xFF2563EB)),
                start = Offset(cx - boxSize * 0.5f, cy - boxSize * 0.5f),
                end = Offset(cx + boxSize * 0.5f, cy + boxSize * 0.5f)
            ),
            topLeft = Offset(cx - boxSize * 0.5f, cy - boxSize * 0.5f),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Safe door inset
        val inset = 4.dp.toPx()
        drawRoundRect(
            color = Color(0xFF1D4ED8),
            topLeft = Offset(cx - boxSize * 0.5f + inset, cy - boxSize * 0.5f + inset),
            size = Size(boxSize - inset * 2, boxSize - inset * 2),
            cornerRadius = CornerRadius(5.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Combination dial
        drawCircle(
            color = Color(0xFF93C5FD),
            radius = 9.dp.toPx(),
            center = Offset(cx, cy)
        )
        drawCircle(
            color = Color(0xFF1E3A8A),
            radius = 5.dp.toPx(),
            center = Offset(cx, cy)
        )
        // Dial handle notch
        drawLine(
            Color.White,
            Offset(cx, cy),
            Offset(cx + 6.dp.toPx(), cy),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

@Composable
fun IntruderGuardIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f

        // Circular background glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.25f), Color.Transparent),
                center = Offset(cx, cy),
                radius = 34.dp.toPx()
            ),
            radius = 34.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Security dome camera body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF475569), Color(0xFF0F172A)),
                center = Offset(cx, cy),
                radius = 18.dp.toPx()
            ),
            radius = 18.dp.toPx(),
            center = Offset(cx, cy)
        )
        // Lens ring
        drawCircle(
            color = Color(0xFF0284C7),
            radius = 11.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 2.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF020617),
            radius = 7.dp.toPx(),
            center = Offset(cx, cy)
        )
        // Specular glint
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = 2.5.dp.toPx(),
            center = Offset(cx - 2.5.dp.toPx(), cy - 2.5.dp.toPx())
        )
        // Red alert LED dot
        drawCircle(
            color = Color(0xFFEF4444),
            radius = 3.5.dp.toPx(),
            center = Offset(cx + 14.dp.toPx(), cy - 11.dp.toPx())
        )
    }
}
