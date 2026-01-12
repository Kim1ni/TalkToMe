package com.kmp.talktome.ui.screens.session.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kmp.talktome.ui.theme.TalkToMeTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.session_dialog_end_title
import talktome.composeapp.generated.resources.session_end_dialog_cancel
import talktome.composeapp.generated.resources.session_end_dialog_end
import talktome.composeapp.generated.resources.session_end_dialog_text


@Composable
fun EndSessionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.session_dialog_end_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = { Text(text = stringResource(Res.string.session_end_dialog_text)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = stringResource(Res.string.session_end_dialog_end))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.session_end_dialog_cancel))
            }
        }
    )
}


@Preview
@Composable
fun EndSessionDialogPreview() {
    TalkToMeTheme {
        EndSessionDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}