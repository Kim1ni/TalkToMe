package com.kmp.talktome.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_arrowback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseHeader(
    onBackPressed: () -> Unit = {},
    title: String,
    subtitle: String = ""
) {
    TopAppBar(
        title =  {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,//surface,
            titleContentColor = MaterialTheme.colorScheme.onBackground//onSurface
        ),
        navigationIcon = {
            if (onBackPressed == {}) {
                IconButton(
                    onClick = onBackPressed
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_arrowback),
                        contentDescription = "Navigate Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        modifier = Modifier.wrapContentHeight()
    )
}