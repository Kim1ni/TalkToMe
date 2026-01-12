package com.kmp.talktome.ui.screens.home.composables

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kmp.talktome.domain.model.TodoCategory
import com.kmp.talktome.domain.model.TodoItem
import com.kmp.talktome.ui.screens.home.models.CardStackPosition
import com.kmp.talktome.ui.screens.home.models.CardStackPosition.BOTTOM
import com.kmp.talktome.ui.screens.home.models.CardStackPosition.SINGLE
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_check
import talktome.composeapp.generated.resources.icon_clock
//import talktome.composeapp.generated.resources.icon_brain
//import talktome.composeapp.generated.resources.icon_muscle
import talktome.composeapp.generated.resources.icon_quote
//import talktome.composeapp.generated.resources.icon_social
import talktome.composeapp.generated.resources.icon_trash

@Composable
fun TodoItemCard(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    position: CardStackPosition
) {
    val backgroundColor = if (todo.completed) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentAlpha = if (todo.completed) 0.6f else 1f

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = position.getShape(cornerRadius = 16.dp),
        color = backgroundColor,
        shadowElevation = if (todo.completed) 0.dp else 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = todo.completed,
                        onCheckedChange = { onToggle() },
                        modifier = Modifier.size(24.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        todo.category?.let { category ->
                            TaskPillLabel(
                                text = category.name,
                                color = Color(todo.getCategoryColor()),
                                icon = when (category) {
                                    TodoCategory.COGNITIVE -> painterResource(Res.drawable.icon_clock)
                                    TodoCategory.BEHAVIORAL -> painterResource(Res.drawable.icon_check)
                                    TodoCategory.SOCIAL -> painterResource(Res.drawable.icon_quote)
                                }
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                        
                        Text(
                            text = todo.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                            textDecoration = if (todo.completed) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_trash),
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = if (todo.completed) 0.5f else 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Reflection Section
                if (todo.completed && todo.reflection != null) {
                    ReflectionPill(
                        mood = todo.reflection.mood,
                        notes = todo.reflection.notes
                    )
                }
            }

            if (position != BOTTOM && position != SINGLE) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun TaskPillLabel(text: String, color: Color, icon: Painter) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReflectionPill(mood: String, notes: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(Res.drawable.icon_quote),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Reflected: $mood",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (notes.isNotBlank()) {
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        }
    }
}
