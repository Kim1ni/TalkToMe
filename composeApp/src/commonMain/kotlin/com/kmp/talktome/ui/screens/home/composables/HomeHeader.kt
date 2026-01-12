package com.kmp.talktome.ui.screens.home.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kmp.talktome.ui.theme.TalkToMeTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.home_profile_picture
import talktome.composeapp.generated.resources.home_subtitle
import talktome.composeapp.generated.resources.home_welcome

@Composable
fun HomeHeader(
    userName: String,
    profilePictureUrl: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
    ) {

        AsyncImage(
            model = profilePictureUrl, //?: Icon(painter =  painterResource(Res.drawable.icon_default_avatar), contentDescription = stringResource(Res.string.home_profile_picture)),
            contentDescription = stringResource(Res.string.home_profile_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        )
        Column {
            Text(
                text = stringResource(Res.string.home_welcome, userName),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(Res.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
@Preview
fun HomeHeaderPreview() {
    TalkToMeTheme {
        HomeHeader(
            userName = "Gabriel"
        )
    }
}