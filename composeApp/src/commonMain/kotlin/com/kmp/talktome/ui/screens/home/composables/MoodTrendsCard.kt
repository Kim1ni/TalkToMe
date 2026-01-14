package com.kmp.talktome.ui.screens.home.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.ui.screens.home.models.ChartDataPoint
import com.kmp.talktome.domain.util.toFormattedDate
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.common.DashedShape
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.common.Insets
import com.patrykandpatrick.vico.multiplatform.common.LayeredComponent
import com.patrykandpatrick.vico.multiplatform.common.component.ShapeComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberTextComponent

private data class PreparedChartPoint(
    val x: Double,
    val y: Double,
    val label: String
)

@Composable
fun MoodTrendsCard(
    chartData: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    val preparedPoints = remember(chartData) {
        chartData.mapIndexed { index, point ->
            PreparedChartPoint(
                x = index.toDouble(),
                y = point.sentimentScore.toDouble(),
                label = point.date.toFormattedDate()
            )
        }
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    val lineColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    // 1. Properly remember the Area Fill
    val areaFill = remember(lineColor) {
        LineCartesianLayer.AreaFill.single(
            fill = Fill(
                brush = Brush.verticalGradient(
                    listOf(lineColor.copy(alpha = 0.4f), lineColor.copy(alpha = 0.0f))
                )
            )
        )
    }

    // 2. Properly remember the Label Component
    val labelComponent = rememberTextComponent(
        style = MaterialTheme.typography.bodySmall,
        //color = labelColor
    )

    // 3. Move the chart instance into its own remember block
    val marker = rememberMarker()
    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            LineCartesianLayer.rememberLine(
                fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                areaFill = areaFill,
                pointConnector = LineCartesianLayer.PointConnector.cubic(curvature = 0.4f),
                pointProvider = LineCartesianLayer.PointProvider.single(
                    LineCartesianLayer.Point(
                        rememberShapeComponent(Fill(lineColor), CircleShape)
                    )
                ),
            )
        )
    )

    val chart = rememberCartesianChart(
        lineLayer,
        startAxis = VerticalAxis.rememberStart(
            guideline = null,
            label = labelComponent,
            horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            label = labelComponent,
            valueFormatter = { _, value, _ ->
                preparedPoints.getOrNull(value.toInt())?.label ?: ""
            }
        ),
        marker = marker
    )

    LaunchedEffect(chartData) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = chartData.indices.map { it.toDouble() },
                    y = chartData.map { it.sentimentScore }
                )
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood Trends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Last 7 days",
                    style = MaterialTheme.typography.bodySmall,
                    color = labelColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Use the remembered chart instance
            CartesianChartHost(
                chart = chart,
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
    }
}

@Composable
fun rememberMarker(): CartesianMarker {
    val labelBackground = rememberShapeComponent(
        fill = Fill(MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp),
        //shadow = Shadow(radiusDp = 4f, color = 0x20000000)
    )
    val label = rememberTextComponent(
        background = labelBackground,
        padding = Insets(8.dp, 4.dp),
    )

    val primaryBackground = MaterialTheme.colorScheme.primary

    val indicatorPoint = rememberShapeComponent(fill = Fill(MaterialTheme.colorScheme.primary), shape = CircleShape)
    val indicator = remember(indicatorPoint) {
        LayeredComponent(
            back = ShapeComponent(
                fill = Fill(primaryBackground.copy(alpha = 0.3f)),
                shape = CircleShape
            ),
            front = indicatorPoint,
            padding = Insets(5.dp),
        )
    }

    val guideline = rememberLineComponent(
        fill = Fill(MaterialTheme.colorScheme.outlineVariant),
        thickness = 2.dp,
        shape = DashedShape(shape = RoundedCornerShape(8.dp), dashLength = 8.dp, gapLength = 4.dp)
    )

    return rememberDefaultCartesianMarker(
        label = label,
        indicator = { indicator },
        guideline = guideline,
    )
}


private fun formatDateLabel(chartData: List<ChartDataPoint>, index: Int): String {
    if (index < 0 || index >= chartData.size) return ""

    return try {
        chartData[index].date.toFormattedDate()
    } catch (e: Exception) {
        ""
    }
}