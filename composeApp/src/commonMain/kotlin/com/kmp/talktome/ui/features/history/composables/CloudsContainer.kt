package com.kmp.talktome.ui.features.history.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.Session

@Composable
fun CloudsContainer(
    sessions: List<Session>,
    onSessionClick: (Session) -> Unit
) {
    if (sessions.isEmpty()) {
        EmptyCloudState()
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 32.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
            verticalItemSpacing = 32.dp
        ) {
            itemsIndexed(sessions) { index, session ->
                CloudCard(
                    session = session,
                    index = index,
                    onClick = { onSessionClick(session) }
                )
            }
        }
    }
}