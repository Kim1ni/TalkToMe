package com.kmp.talktome.domain.notifications

import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationOptionBadge
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IOSNotificationManager : NotificationManager {
    override fun areNotificationsBlocked(): Boolean {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        var status = UNAuthorizationStatusNotDetermined
        
        // Since we need to return a Boolean synchronously but iOS API is async
        // In a real KMP app, these might be suspend functions, but here they are not.
        // For simplicity in this implementation, we might use a blocking call if possible or just return a default if it's too complex to block.
        // However, usually we want to avoid runBlocking on main thread in iOS.
        
        // Let's see if we can use a more KMP friendly way or if we should just implement it as best as possible.
        // Actually, many KMP implementations use runBlocking for these platform checks if the interface is synchronous.
        
        runBlocking {
            status = getAuthorizationStatus()
        }
        
        return status == UNAuthorizationStatusDenied
    }

    override fun hasNotificationPermission(): Boolean {
        var status = UNAuthorizationStatusNotDetermined
        runBlocking {
            status = getAuthorizationStatus()
        }
        return status == UNAuthorizationStatusAuthorized
    }

    private suspend fun getAuthorizationStatus(): UNAuthorizationStatus = suspendCoroutine { continuation ->
        UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
            continuation.resume(settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined)
        }
    }

    override fun showTestNotification(
        appName: String,
        message: String
    ): Boolean {
        val content = UNMutableNotificationContent()
        content.setTitle(appName)
        content.setBody(message)
        content.setSound(platform.UserNotifications.UNNotificationSound.defaultSound())

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "test_notification",
            content = content,
            trigger = null // Deliver immediately
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error showing notification: ${error.localizedDescription}")
            }
        }
        return true
    }

    override fun requestNotificationPermission(): Boolean {
        var grantedResult = false
        runBlocking {
            grantedResult = suspendCoroutine { continuation ->
                val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
                UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(options) { granted, error ->
                    continuation.resume(granted)
                }
            }
        }
        return grantedResult
    }
}

actual fun getNotificationManager(): NotificationManager = IOSNotificationManager()