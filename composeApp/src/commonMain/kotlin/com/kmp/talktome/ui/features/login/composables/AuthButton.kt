package com.kmp.talktome.ui.features.login.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.kmp.talktome.ui.theme.TalkToMeTheme
import org.jetbrains.compose.resources.stringResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.login_connecting

@Composable
fun AuthButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    colors: ButtonColors = ButtonDefaults.buttonColors(containerColor = Color.White),
    contentDescription: String? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = TalkToMeTheme.extendedColorScheme.actionGreen.color,
                shape = shape
            )
            .height(40.dp),
        colors = colors,
        shape = shape,
        enabled = !isLoading
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isLoading
            ) { isLoading ->
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = stringResource(Res.string.login_connecting),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = icon,
                            contentDescription = contentDescription,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = text,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

