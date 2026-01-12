package com.kmp.talktome.domain.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManagerSystem
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class AndroidNotificationManager(private val context: Context) : NotificationManager {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val channelId = "talktome_notifications"
    private val channelName = "TalkToMe Notifications"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = AndroidNotificationManagerSystem.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "General notifications for TalkToMe"
            }
            val systemNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManagerSystem
            systemNotificationManager.createNotificationChannel(channel)
        }
    }

    override fun areNotificationsBlocked(): Boolean {
        return !notificationManager.areNotificationsEnabled()
    }

    override fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun showTestNotification(
        appName: String,
        message: String
    ): Boolean {
        return try {
            if (!hasNotificationPermission()) return false

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Use app icon
                .setContentTitle(appName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            try {
                notificationManager.notify(1, builder.build())
                true
            } catch (e: SecurityException) {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun requestNotificationPermission(): Boolean {
        // This usually requires an Activity. 
        // In KMP, we might need a different approach or just return based on current state.
        // For now, return the current state.
        return hasNotificationPermission()
    }
}

private var appContext: Context? = null

fun initNotificationManager(context: Context) {
    appContext = context
}

actual fun getNotificationManager(): NotificationManager {
    return AndroidNotificationManager(appContext ?: throw IllegalStateException("NotificationManager not initialized with context"))
}