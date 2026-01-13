package com.kmp.talktome

import com.kmp.talktome.domain.util.TIME_ZONE
import com.kmp.talktome.domain.util.getNotificationTime
import io.github.aakira.napier.Napier
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.datetime.toNSTimeZone
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateComponents
import platform.Foundation.NSError
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

class IOSLocalNotificationService: LocalNotificationService {
    companion object {
        const val LOCAL_NOTIFICATION_ID_KEY = "localNotificationId"
        private const val LOG_TAG = "IOSNotificationService"
    }

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            notificationCenter.requestAuthorizationWithOptions(UNAuthorizationOptionAlert) { granted, error ->
                if (error != null) {
                    Napier.e("Notification permission request failed with error: $error", tag = LOG_TAG)
                }
                continuation.resume(granted)
            }
        }
    }

    override fun post(
        localNotificationId: LocalNotificationId,
        title: String,
        message: String,
        time: LocalDateTime?
    ) {
        Napier.d("Posting: $time, $localNotificationId, $title, $message", tag = LOG_TAG)

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setUserInfo(mapOf(LOCAL_NOTIFICATION_ID_KEY to localNotificationId.toString()))
        }
        val trigger = if (time != null) {
            val adjustedTime = getNotificationTime(time)
            UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = NSDateComponents().apply {
                    year = adjustedTime.year.toLong()
                    month = adjustedTime.month.number.toLong()
                    day = adjustedTime.day.toLong()
                    hour = adjustedTime.hour.toLong()
                    minute = adjustedTime.minute.toLong()
                    second = adjustedTime.second.toLong()
                    val calendar = NSCalendar.currentCalendar
                    calendar.setTimeZone(TIME_ZONE.toNSTimeZone())
                    this.calendar = calendar
                },
                repeats = false,
            )
        } else {
            null
        }
        val request = UNNotificationRequest.requestWithIdentifier(localNotificationId.toString(), content, trigger)
        notificationCenter.addNotificationRequest(request) { error: NSError? ->
            if (error != null) {
                Napier.e("Notification request failed with error: $error", tag = LOG_TAG)
            } else {
                Napier.d("Notification request completed successfully", tag = LOG_TAG)
            }
        }
    }
    

    override fun cancel(localNotificationId: LocalNotificationId) {
        Napier.d("Cancelling: $localNotificationId", tag = LOG_TAG)

        val identifiers = listOf(localNotificationId.toString())
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifiers)
    }
}
