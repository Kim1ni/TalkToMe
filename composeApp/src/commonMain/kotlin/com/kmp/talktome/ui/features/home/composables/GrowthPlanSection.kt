package com.kmp.talktome.ui.features.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import talktome.composeapp.generated.resources.Res
import talktome.composeapp.generated.resources.icon_notifications
import talktome.composeapp.generated.resources.icon_trash

/*
import talktome.composeapp.generated.resources.icon_brain
import talktome.composeapp.generated.resources.icon_delete
import talktome.composeapp.generated.resources.icon_muscle
import talktome.composeapp.generated.resources.icon_notification
import talktome.composeapp.generated.resources.icon_social
*/

@Composable
fun GrowthPlanSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        /*
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Growth Plan (Your Tasks)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("6 Active", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        // This would typically come from a ViewModel
        val taskItems = listOf(
            TaskPill("COGNITIVE", TalkToMeTheme.extendedColorScheme.cognitive.color, Res.drawable.icon_brain),
            TaskPill("BEHAVIOURAL", TalkToMeTheme.extendedColorScheme.behavioural.color, Res.drawable.icon_muscle),
            TaskPill("SOCIAL", TalkToMeTheme.extendedColorScheme.social.color, Res.drawable.icon_social)
        )
        taskItems.forEach { task ->
            GrowthTaskItem(taskPill = task)
        }*/
    }
}

// 1. Change the icon type to DrawableResource
data class TaskPill(val text: String, val color: Color, val icon: DrawableResource)

@Composable
fun GrowthTaskItem(taskPill: TaskPill) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = false, onClick = { /*TODO*/ }, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 2. The painterResource call now works perfectly with the DrawableResource
                TaskPillLabel(taskPill.text, taskPill.color, painterResource(taskPill.icon))
                Text(
                    "Identify specific thoughts or beliefs that make you feel normal when experiencing exhaustion",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 16.sp
                )
            }
            Spacer(Modifier.width(16.dp))
            // 3. Use your custom drawable for the action icons
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(Res.drawable.icon_notifications), contentDescription = "Remind", tint = Color.Gray)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(Res.drawable.icon_trash), contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}

// 4. Update the TaskPillLabel to accept a Painter
@Composable
fun TaskPillLabel(text: String, color: Color, icon: Painter) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = CircleShape
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(text, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}
