package com.kmp.talktome.domain.notifications

interface NotificationManager {

    fun areNotificationsBlocked(): Boolean

    fun hasNotificationPermission(): Boolean

    fun showTestNotification(
        appName: String,
        message: String
    ): Boolean

    fun requestNotificationPermission(): Boolean

}

expect fun getNotificationManager(): NotificationManager