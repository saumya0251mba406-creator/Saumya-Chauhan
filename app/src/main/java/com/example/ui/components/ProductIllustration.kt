package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PackagingVariant
import com.example.data.Product
import com.example.data.StarterBundle

@Composable
fun ProductIllustration(
    product: Product,
    modifier: Modifier = Modifier,
    height: Dp = 176.dp,
) {
    val cfg = product.packagingConfig
    val bodyColor = Color(cfg.bodyHex)
    val accentColor = Color(cfg.accentHex)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .semantics(mergeDescendants = true) {
                contentDescription = cfg.contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        // Subtle organic backdrop arch
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = accentColor.copy(alpha = 0.08f),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.5f, size.height * 0.54f),
            )
            drawOval(
                color = Color(0xFF212622).copy(alpha = 0.09f),
                topLeft = Offset(size.width * 0.28f, size.height * 0.85f),
                size = Size(size.width * 0.44f, size.height * 0.06f),
            )
        }

        // Bottle / Tube / Dropper vector drawn dynamically in Canvas + crisp Nivora label overlay
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            val canvasHeight = maxHeight
            val vesselWidth = when (cfg.variant) {
                PackagingVariant.CLEANSER_TUBE -> (canvasHeight * 0.46f).coerceIn(58.dp, 86.dp)
                PackagingVariant.LOTION_PUMP -> (canvasHeight * 0.50f).coerceIn(64.dp, 94.dp)
                PackagingVariant.SHAMPOO_BOTTLE -> (canvasHeight * 0.44f).coerceIn(56.dp, 84.dp)
                PackagingVariant.SERUM_DROPPER -> (canvasHeight * 0.38f).coerceIn(50.dp, 74.dp)
            }

            Box(
                modifier = Modifier
                    .width(vesselWidth)
                    .height(canvasHeight * 0.88f),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val outline = Color(0xFF333A34).copy(alpha = 0.32f)

                    when (cfg.variant) {
                        PackagingVariant.CLEANSER_TUBE -> {
                            // Top-down tube crimp at bottom? No, standing cap-down cleanser tube
                            val capW = w * 0.48f
                            val capH = h * 0.14f
                            // Tube body tapered
                            val tubePath = Path().apply {
                                moveTo(w * 0.06f, h * 0.06f)
                                lineTo(w * 0.94f, h * 0.06f)
                                lineTo(w * 0.78f, h * 0.84f)
                                lineTo(w * 0.22f, h * 0.84f)
                                close()
                            }
                            drawPath(tubePath, color = bodyColor)
                            drawPath(tubePath, color = outline, style = Stroke(width = 2.2f))
                            // Top crimped seal strip
                            drawRoundRect(
                                color = accentColor.copy(alpha = 0.25f),
                                topLeft = Offset(w * 0.06f, h * 0.04f),
                                size = Size(w * 0.88f, h * 0.06f),
                                cornerRadius = CornerRadius(4f, 4f),
                            )
                            // Flip cap at bottom
                            drawRoundRect(
                                color = accentColor,
                                topLeft = Offset((w - capW) / 2f, h * 0.83f),
                                size = Size(capW, capH),
                                cornerRadius = CornerRadius(8f, 8f),
                            )
                        }

                        PackagingVariant.LOTION_PUMP -> {
                            // Pump head & stem
                            drawRoundRect(
                                color = accentColor,
                                topLeft = Offset(w * 0.34f, h * 0.02f),
                                size = Size(w * 0.44f, h * 0.055f),
                                cornerRadius = CornerRadius(8f, 8f),
                            )
                            drawRect(
                                color = accentColor.copy(alpha = 0.85f),
                                topLeft = Offset(w * 0.44f, h * 0.07f),
                                size = Size(w * 0.12f, h * 0.10f),
                            )
                            // Collar
                            drawRoundRect(
                                color = accentColor,
                                topLeft = Offset(w * 0.36f, h * 0.16f),
                                size = Size(w * 0.28f, h * 0.07f),
                                cornerRadius = CornerRadius(6f, 6f),
                            )
                            // Rounded bottle body
                            drawRoundRect(
                                color = bodyColor,
                                topLeft = Offset(w * 0.06f, h * 0.23f),
                                size = Size(w * 0.88f, h * 0.73f),
                                cornerRadius = CornerRadius(24f, 24f),
                            )
                            drawRoundRect(
                                color = outline,
                                topLeft = Offset(w * 0.06f, h * 0.23f),
                                size = Size(w * 0.88f, h * 0.73f),
                                cornerRadius = CornerRadius(24f, 24f),
                                style = Stroke(width = 2.2f),
                            )
                        }

                        PackagingVariant.SHAMPOO_BOTTLE -> {
                            // Disc cap
                            drawRoundRect(
                                color = accentColor,
                                topLeft = Offset(w * 0.30f, h * 0.04f),
                                size = Size(w * 0.40f, h * 0.11f),
                                cornerRadius = CornerRadius(10f, 10f),
                            )
                            // Tall cylindrical bottle
                            drawRoundRect(
                                color = bodyColor,
                                topLeft = Offset(w * 0.08f, h * 0.15f),
                                size = Size(w * 0.84f, h * 0.81f),
                                cornerRadius = CornerRadius(22f, 22f),
                            )
                            drawRoundRect(
                                color = outline,
                                topLeft = Offset(w * 0.08f, h * 0.15f),
                                size = Size(w * 0.84f, h * 0.81f),
                                cornerRadius = CornerRadius(22f, 22f),
                                style = Stroke(width = 2.2f),
                            )
                        }

                        PackagingVariant.SERUM_DROPPER -> {
                            // Dropper rubber bulb
                            drawRoundRect(
                                color = Color(0xFF3E443E),
                                topLeft = Offset(w * 0.36f, h * 0.04f),
                                size = Size(w * 0.28f, h * 0.17f),
                                cornerRadius = CornerRadius(22f, 22f),
                            )
                            // Collar
                            drawRoundRect(
                                color = accentColor,
                                topLeft = Offset(w * 0.30f, h * 0.19f),
                                size = Size(w * 0.40f, h * 0.08f),
                                cornerRadius = CornerRadius(8f, 8f),
                            )
                            // Glass serum bottle
                            drawRoundRect(
                                color = bodyColor,
                                topLeft = Offset(w * 0.10f, h * 0.27f),
                                size = Size(w * 0.80f, h * 0.69f),
                                cornerRadius = CornerRadius(18f, 18f),
                            )
                            drawRoundRect(
                                color = outline,
                                topLeft = Offset(w * 0.10f, h * 0.27f),
                                size = Size(w * 0.80f, h * 0.69f),
                                cornerRadius = CornerRadius(18f, 18f),
                                style = Stroke(width = 2.2f),
                            )
                        }
                    }
                }

                // Consistent Nivora label band on vessel
                Column(
                    modifier = Modifier
                        .padding(
                            top = when (cfg.variant) {
                                PackagingVariant.CLEANSER_TUBE -> 6.dp
                                PackagingVariant.SERUM_DROPPER -> 26.dp
                                else -> 16.dp
                            },
                            start = 8.dp,
                            end = 8.dp,
                        )
                        .fillMaxWidth(0.76f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.86f))
                        .border(0.75.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(vertical = 5.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "NIVORA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color = accentColor,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = product.routineStep.uppercase(),
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3E453F),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                    Text(
                        text = product.size,
                        fontSize = 7.sp,
                        color = Color(0xFF666E67),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun BundleIllustration(
    bundle: StarterBundle,
    modifier: Modifier = Modifier,
    height: Dp = 152.dp,
) {
    val products = bundle.includedProducts
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(10.dp)
            .semantics(mergeDescendants = true) {
                contentDescription =
                    "Illustrative Nivora ${bundle.name} starter bundle featuring ${products.joinToString(" and ") { it.name }}"
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            products.forEach { product ->
                ProductIllustration(
                    product = product,
                    modifier = Modifier.weight(1f),
                    height = height - 20.dp,
                )
            }
        }
    }
}

@Composable
fun NivoraLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
) {
    val sage = MaterialTheme.colorScheme.primary
    val cream = MaterialTheme.colorScheme.onPrimary
    Canvas(modifier = modifier.size(size)) {
        drawCircle(color = sage)
        val w = this.size.width
        val h = this.size.height
        val droplet = Path().apply {
            moveTo(w * 0.50f, h * 0.22f)
            cubicTo(w * 0.50f, h * 0.22f, w * 0.28f, h * 0.47f, w * 0.28f, h * 0.62f)
            cubicTo(w * 0.28f, h * 0.74f, w * 0.38f, h * 0.82f, w * 0.50f, h * 0.82f)
            cubicTo(w * 0.62f, h * 0.82f, w * 0.72f, h * 0.74f, w * 0.72f, h * 0.62f)
            cubicTo(w * 0.72f, h * 0.47f, w * 0.50f, h * 0.22f, w * 0.50f, h * 0.22f)
            close()
        }
        drawPath(droplet, color = cream)
        drawLine(
            color = sage,
            start = Offset(w * 0.50f, h * 0.44f),
            end = Offset(w * 0.50f, h * 0.72f),
            strokeWidth = w * 0.06f,
        )
    }
}
