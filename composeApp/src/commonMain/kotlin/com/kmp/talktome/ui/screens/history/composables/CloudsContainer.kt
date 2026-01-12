package com.kmp.talktome.ui.screens.history.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.Session
import com.kmp.talktome.domain.util.formatDateRelative
import kotlinx.datetime.LocalDate

@Composable
fun CloudsContainer(
    sessions: Map<LocalDate, List<Session>>,
    onSessionClick: (Session) -> Unit
) {
    if (sessions.isEmpty()) {
        EmptyCloudState()
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
        ) {
            sessions.forEach { (date, sessionList) ->
                item(
                    key = "header_$date",
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    DateHeader(
                        date = formatDateRelative(date),
                    )
                }

                itemsIndexed(
                    items = sessionList,
                    key = { _, session -> session.id } // Use a unique ID if available
                ) { index, session ->
                    CloudCard(
                        session = session,
                        index = index,
                        onClick = { onSessionClick(session) }
                    )
                }
            }
        }
    }
}