/*package com.kmp.talktome.ui.screens.session_details.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.util.toFormattedDate
import com.kmp.talktome.domain.util.toFormattedTime
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_arrowback
import talktome.composeapp.generated.resources.icon_calendar_month
import talktome.composeapp.generated.resources.icon_clock
import talktome.composeapp.generated.resources.icon_download
import talktome.composeapp.generated.resources.icon_export

// SessionDetailsHeader.kt
@Composable
fun SessionDetailsHeader(
    session: Session,
    onBackPressed: () -> Unit,
    onDownloadAudio: () -> Unit,
    onExportPdf: () -> Unit,
    isDownloading: Boolean,
    hasAudio: Boolean
) {
    val date = remember(session.timestamp) {
        session.timestamp.toFormattedDate()
    }

    val time = remember(session.timestamp) {
        session.timestamp.toFormattedTime()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF075E54),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 24.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier.offset(x = (-8).dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_arrowback),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Session Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (hasAudio) {
                        IconButton(onClick = onDownloadAudio) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_download),
                                    contentDescription = "Download Audio",
                                    tint = Color(0xFFDCF8C6)
                                )
                            }
                        }
                    }
                    IconButton(onClick = onExportPdf) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_export),
                            contentDescription = "Export PDF",
                            tint = Color(0xFFDCF8C6)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_calendar_month),
                            contentDescription = null,
                            tint = Color(0xFFDCF8C6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFDCF8C6)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_clock),
                            contentDescription = null,
                            tint = Color(0xFFDCF8C6).copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFDCF8C6).copy(alpha = 0.8f)
                        )
                    }
                }

                session.analysis?.let { analysis ->
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${analysis.sentimentScore}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF25D366)
                        )
                        Text(
                            text = "MOOD SCORE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    }
}
*/
package com.kmp.talktome.ui.screens.session_details.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.util.toFormattedDate
import com.kmp.talktome.domain.util.toFormattedTime
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_arrowback
import talktome.composeapp.generated.resources.icon_calendar_month
import talktome.composeapp.generated.resources.icon_clock
import talktome.composeapp.generated.resources.icon_download
import talktome.composeapp.generated.resources.icon_export

@Composable
fun SessionDetailsHeader(
    session: Session,
    onBackPressed: () -> Unit,
    onDownloadAudio: () -> Unit,
    onExportPdf: () -> Unit,
    isDownloading: Boolean,
    hasAudio: Boolean
) {
    val date = remember(session.timestamp) {
        session.timestamp.toFormattedDate()
    }

    val time = remember(session.timestamp) {
        session.timestamp.toFormattedTime()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackPressed
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_arrowback),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Session Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (hasAudio) {
                        IconButton(onClick = onDownloadAudio) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_download),
                                    contentDescription = "Download Audio",
                                    // Use primaryContainer for contrasting action icons
                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    IconButton(onClick = onExportPdf) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_export),
                            contentDescription = "Export PDF",
                            tint = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_calendar_month),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_clock),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                }

                session.analysis?.let { analysis ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(analysis.getSentimentColor()).copy(0.5f),
                        border = BorderStroke(1.dp, Color(analysis.getSentimentColor()))
                    ) {
                        Text(
                            text = "${analysis.sentimentScore} - ${analysis.getSentimentLabel()}",
                            color = Color(analysis.getSentimentColor()),
                            modifier = Modifier.padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            ),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
