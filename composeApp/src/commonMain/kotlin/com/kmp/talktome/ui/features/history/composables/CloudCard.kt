package com.kmp.talktome.ui.features.history.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.util.toFormattedDate
import com.kmp.talktome.ui.features.history.models.CloudShape
import com.kmp.talktome.ui.features.history.models.CloudShape.Companion.getCloudStyle

@Composable
fun CloudCard(
    session: Session,
    index: Int,
    onClick: () -> Unit
) {
    val cloudStyle = remember(session.analysis?.sentimentScore, index) {
        getCloudStyle(session.analysis?.sentimentScore ?: 50, index)
    }

    val scale by animateFloatAsState(
        targetValue = cloudStyle.scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val date = remember(session.timestamp) { session.timestamp.toFormattedDate()
    }

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(128.dp)
            .graphicsLayer {
                scaleX = scale * pressScale
                scaleY = scale * pressScale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        // Cloud shape with custom path
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    // Draw shadow
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = cloudStyle.shadowColor
                            // TODO Add Mask Filter maskFilter = BlurMaskFilter(30f, BlurMaskFilter.Blur.NORMAL)
                        }
                        canvas.drawRoundRect(
                            left = 0f,
                            top = 20f,
                            right = size.width,
                            bottom = size.height + 20f,
                            radiusX = size.width * 0.3f,
                            radiusY = size.height * 0.3f,
                            paint = paint//.asFrameworkPaint()//.asFrameworkPaint()
                        )
                    }
                }
        ) {
            // Draw cloud shape
            val path = Path().apply {
                when (cloudStyle.shapeType) {
                    CloudShape.ROUND -> {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(Offset.Zero, size),
                                cornerRadius = CornerRadius(size.width * 0.5f)
                            )
                        )
                    }
                    CloudShape.ORGANIC_1 -> {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(Offset.Zero, size),
                                topLeft = CornerRadius(size.width * 0.6f, size.height * 0.6f),
                                topRight = CornerRadius(size.width * 0.4f, size.height * 0.3f),
                                bottomRight = CornerRadius(size.width * 0.5f, size.height * 0.7f),
                                bottomLeft = CornerRadius(size.width * 0.7f, size.height * 0.4f)
                            )
                        )
                    }
                    CloudShape.ORGANIC_2 -> {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(Offset.Zero, size),
                                topLeft = CornerRadius(size.width * 0.3f, size.height * 0.3f),
                                topRight = CornerRadius(size.width * 0.7f, size.height * 0.3f),
                                bottomRight = CornerRadius(size.width * 0.7f, size.height * 0.7f),
                                bottomLeft = CornerRadius(size.width * 0.3f, size.height * 0.7f)
                            )
                        )
                    }
                    CloudShape.ORGANIC_3 -> {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(Offset.Zero, size),
                                topLeft = CornerRadius(size.width * 0.7f, size.height * 0.6f),
                                topRight = CornerRadius(size.width * 0.3f, size.height * 0.4f),
                                bottomRight = CornerRadius(size.width * 0.3f, size.height * 0.6f),
                                bottomLeft = CornerRadius(size.width * 0.7f, size.height * 0.4f)
                            )
                        )
                    }
                }
            }

            // Draw gradient fill
            drawPath(
                path = path,
                brush = cloudStyle.gradientBrush
            )

            // Draw border
            drawPath(
                path = path,
                color = cloudStyle.borderColor,
                style = Stroke(width = 4f)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = cloudStyle.textColor.copy(alpha = 0.6f),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = session.analysis?.mood ?: "Reflection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = cloudStyle.textColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            session.analysis?.sentimentScore?.let { score ->
                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(2.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(score / 100f)
                            .background(
                                color = cloudStyle.textColor.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}
