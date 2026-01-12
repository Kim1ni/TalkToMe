@file: OptIn(ExperimentalTime::class)
package com.kmp.talktome.domain.util

import com.kmp.talktome.domain.model.TodoItem
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

val dateFormatter = LocalDateTime.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    day()
    chars(", ")
    year()
}

val timeFormatter = LocalDateTime.Format {
    hour()
    char(':')
    minute()
}
/*
val dateTimeFormatter = LocalDateTime.Format {
    append(dateFormatter)//TODO: Fix date formatting
    char(' ')
    append(timeFormatter) //TODO: Fix time formatting
}*/

fun Long.toFormattedDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.format(dateFormatter)
}

fun Long.toFormattedTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.format(timeFormatter)
}
/*
fun Long.toFormattedDateTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.format(dateTimeFormatter)
}*/

fun TodoItem.getDaysUntilDue(): Int? {
    val dueTimestamp = this.dueDateTimestamp ?: return null // Assume nullable Long
    val tz = TimeZone.currentSystemDefault()

    val today = Clock.System.now().toLocalDateTime(tz).date
    val dueDate = Instant.fromEpochMilliseconds(dueTimestamp).toLocalDateTime(tz).date

    return today.daysUntil(dueDate)
}


fun formatDateRelative(date: LocalDate): String {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(tz)
    val yesterday = today.minus(1, DateTimeUnit.DAY)

    // Threshold for "within the last 7 days"
    val sixDaysAgo = today.minus(6, DateTimeUnit.DAY)

    return when {
        date == today -> "Today"
        date == yesterday -> "Yesterday"
        // If the date is within the last week, show the Day Name (e.g., "Tuesday")
        date in sixDaysAgo..<yesterday -> {
            date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        }
        // If same year, show "Month Day" (e.g., "January 15")
        date.year == today.year -> {
            "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.day}"
        }
        // Otherwise, show full date
        else -> "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.day}, ${date.year}"
    }
}


fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        // Format as HH:MM when over an hour
        val hoursString = hours.toString().padStart(2, '0')
        val minutesString = minutes.toString().padStart(2, '0')
        "$hoursString:$minutesString"
    } else {
        // Format as MM:SS when under an hour
        val minutesString = minutes.toString().padStart(2, '0')
        val secondsString = seconds.toString().padStart(2, '0')
        "$minutesString:$secondsString"
    }
}