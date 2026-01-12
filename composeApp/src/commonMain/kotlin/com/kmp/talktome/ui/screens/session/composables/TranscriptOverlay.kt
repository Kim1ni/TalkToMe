package com.kmp.talktome.ui.screens.session.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.PartialTranscript
import com.kmp.talktome.domain.model.SessionStatus
import com.kmp.talktome.domain.model.TranscriptMessage
import com.kmp.talktome.domain.model.TranscriptRole
import com.kmp.talktome.ui.theme.TalkToMeTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.session_prompt_start


@Composable
fun TranscriptOverlay(
    transcript: List<TranscriptMessage>,
    partialTranscript: PartialTranscript?,
    status: SessionStatus
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(transcript.size, partialTranscript) {
        if (transcript.isNotEmpty() || partialTranscript != null) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    maxOf(0, transcript.size)
                )
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(200.dp))
        }

        if (transcript.isEmpty() && partialTranscript == null && status == SessionStatus.ACTIVE) {
            item {
                Text(
                    text = stringResource(Res.string.session_prompt_start),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        items(transcript) { message ->
            TranscriptBubble(message = message)
        }

        if (partialTranscript != null) {
            item {
                TranscriptBubble(
                    message = TranscriptMessage(
                        role = partialTranscript.role,
                        text = partialTranscript.text,
                        timestamp = 0
                    ),
                    isPartial = true
                )
            }
        }
    }
}


@Preview
@Composable
fun TranscriptOverlayPreview() {
    val transcript = listOf(
        TranscriptMessage(
            role = TranscriptRole.USER,
            text = "Hello There!!!",
        ),
        TranscriptMessage(
            role = TranscriptRole.MODEL,
            text = "General Kenobi..."
        ),
        TranscriptMessage(
            role = TranscriptRole.USER,
            text = "Hello There!!!",
        ),
        TranscriptMessage(
            role = TranscriptRole.MODEL,
            text = "General Kenobi..."
        ),TranscriptMessage(
            role = TranscriptRole.USER,
            text = "Hello There!!!",
        ),
        TranscriptMessage(
            role = TranscriptRole.MODEL,
            text = "General Kenobi..."
        ),
    )
    val partialTranscript = PartialTranscript(
        role = TranscriptRole.MODEL,
        text = "I see"
    )
    TalkToMeTheme {
        TranscriptOverlay(
            transcript = transcript,
            partialTranscript = partialTranscript,
            status = SessionStatus.ACTIVE
        )
    }
}