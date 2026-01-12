package com.kmp.talktome.domain.usecase.home

import com.kmp.talktome.domain.model.Session
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class CalculateStreakUseCase {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(sessions: List<Session>): Int {
        if (sessions.isEmpty()) return 0

        val sortedSessions = sessions.sortedByDescending { it.timestamp }
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        var streak = 0
        var checkDate = today

        for (session in sortedSessions) {
            val sessionDate = Instant.fromEpochMilliseconds(session.timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date

            if (sessionDate == checkDate) {
                streak++
                checkDate = checkDate.minus(1, DateTimeUnit.DAY)
            } else if (sessionDate < checkDate) {
                break
            }
        }

        return streak
    }
}
