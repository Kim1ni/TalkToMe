package com.kmp.talktome.ui.screens.history.composables

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.util.toFormattedDate
import com.kmp.talktome.ui.screens.history.models.CloudShape
import com.kmp.talktome.ui.screens.history.models.CloudShape.Companion.getCloudStyle

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
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
    )

    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val dateText = remember(session.timestamp) { session.timestamp.toFormattedDate() }

    Box(
        modifier = Modifier
            .aspectRatio(1.3f) // Slightly wider for grid cells
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = createCloudPath(size, cloudStyle.shapeType)

            drawPath(
                path = path,
                color = cloudStyle.shadowColor.copy(alpha = 0.2f),
                // Shift the shadow down slightly
                /*brush = Brush.verticalGradient(
                    0f to cloudStyle.shadowColor.copy(alpha = 0.2f),
                    size.height to Color.Transparent
                ),*/
                alpha = 0.5f,
                blendMode = BlendMode.ColorBurn,
                colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.2f)),
                style = Stroke(width = 2.dp.toPx())
            )

            // Main Cloud Body
            drawPath(
                path = path,
                brush = cloudStyle.gradientBrush
            )

            // Border
            drawPath(
                path = path,
                color = cloudStyle.borderColor.copy(alpha = 0.4f),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dateText.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = cloudStyle.textColor.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Text(
                text = session.analysis?.mood ?: "Reflection",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = cloudStyle.textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            session.analysis?.sentimentScore?.let { score ->
                Spacer(modifier = Modifier.height(8.dp))
                // Progress bar
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(4.dp)
                        .background(Color.Black.copy(0.08f), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(score / 100f)
                            .background(cloudStyle.textColor.copy(0.4f), RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}


fun createCloudPath(size: Size, shapeType: CloudShape = CloudShape.ROUND): Path {
    return Path().apply {
        val rect = Rect(Offset.Zero, size)
        when (shapeType) {
            CloudShape.ROUND -> {
                addRoundRect(RoundRect(rect, CornerRadius(size.width * 0.5f)))
            }
            CloudShape.ORGANIC_1 -> {
                addRoundRect(RoundRect(rect,
                    topLeft = CornerRadius(size.width * 0.6f, size.height * 0.6f),
                    topRight = CornerRadius(size.width * 0.4f, size.height * 0.3f),
                    bottomRight = CornerRadius(size.width * 0.5f, size.height * 0.7f),
                    bottomLeft = CornerRadius(size.width * 0.7f, size.height * 0.4f)
                ))
            }
            CloudShape.ORGANIC_2 -> {
                addRoundRect(RoundRect(rect,
                    topLeft = CornerRadius(size.width * 0.3f, size.height * 0.3f),
                    topRight = CornerRadius(size.width * 0.7f, size.height * 0.3f),
                    bottomRight = CornerRadius(size.width * 0.7f, size.height * 0.7f),
                    bottomLeft = CornerRadius(size.width * 0.3f, size.height * 0.7f)
                ))
            }
            CloudShape.ORGANIC_3 -> {
                addRoundRect(RoundRect(rect,
                    topLeft = CornerRadius(size.width * 0.7f, size.height * 0.6f),
                    topRight = CornerRadius(size.width * 0.3f, size.height * 0.4f),
                    bottomRight = CornerRadius(size.width * 0.3f, size.height * 0.6f),
                    bottomLeft = CornerRadius(size.width * 0.7f, size.height * 0.4f)
                ))
            }
        }
    }
}