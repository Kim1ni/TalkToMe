package com.kmp.talktome.ui.features.session.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.ui.features.session.formatDuration


@Composable
fun SessionHeader(
    status: SessionStatus,
    duration: Int,
    errorMessage: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF075E54)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Therapy Session",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status badge
            StatusBadge(status = status, errorMessage = errorMessage)

            Spacer(modifier = Modifier.height(16.dp))

            // Timer
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            )
        }
    }
}