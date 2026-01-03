package com.kmp.talktome.domain.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
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

val dateTimeFormatter = LocalDateTime.Format {
    //append(dateFormatter)TODO: Fix date formatting
    char(' ')
    //append(timeFormatter) TODO: Fix time formatting
}

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
}

fun UserState.getStreak(): Int {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date
    var currentStreakDate = today
    var streak = 0

    val sortedSessions = sessions.sortedByDescending { it.timestamp }

    for (session in sortedSessions) {
        val sessionDate = Instant.fromEpochMilliseconds(session.timestamp)
            .toLocalDateTime(tz).date

        if (sessionDate == currentStreakDate) {
            streak++
            // Move "expected" date back by one day
            currentStreakDate = currentStreakDate.minus(1, DateTimeUnit.DAY)
        } else if (sessionDate < currentStreakDate) {
            // Gap in the streak found
            break
        }
        // If sessionDate > currentStreakDate, it's multiple sessions on same day; ignore
    }
    return streak
}

fun TodoItem.getDaysUntilDue(): Int? {
    val dueTimestamp = this.dueDateTimestamp ?: return null // Assume nullable Long
    val tz = TimeZone.currentSystemDefault()

    val today = Clock.System.now().toLocalDateTime(tz).date
    val dueDate = Instant.fromEpochMilliseconds(dueTimestamp).toLocalDateTime(tz).date

    return today.daysUntil(dueDate)
}*/