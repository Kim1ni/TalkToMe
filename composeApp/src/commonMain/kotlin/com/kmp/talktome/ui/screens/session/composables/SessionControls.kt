package com.kmp.talktome.ui.screens.session.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.SessionStatus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_stop
import talktome.composeapp.generated.resources.session_end_button


@Composable
fun SessionControls(
    status: SessionStatus,
    onEndSession: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (status == SessionStatus.ACTIVE) {
            Button(
                onClick = onEndSession,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.icon_stop),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.session_end_button),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Preview
@Composable
fun SessionControlsPreview() {
    SessionControls(
        status = SessionStatus.ACTIVE,
        onEndSession = {}
    )
}