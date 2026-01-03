package com.kmp.talktome.ui.features.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.MoodInsight

@Composable
fun InsightsGrid(
    boosters: List<MoodInsight>,
    drainers: List<MoodInsight>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (boosters.isNotEmpty()) {
            InsightCard(
                title = "Boosters",
                items = boosters,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
        if (drainers.isNotEmpty()) {
            InsightCard(
                title = "Drainers",
                items = drainers,
                color = Color(0xFFEF5350),
                modifier = Modifier.weight(1f)
            )
        }
    }
}