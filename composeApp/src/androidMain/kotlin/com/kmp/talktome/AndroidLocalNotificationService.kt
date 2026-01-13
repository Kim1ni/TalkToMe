package com.kmp.talktome

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.kmp.talktome.domain.util.TIME_ZONE
import com.kmp.talktome.domain.util.getNotificationTime
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import org.koin.mp.KoinPlatform
import kotlin.time.ExperimentalTime


const val EXTRA_LOCAL_NOTIFICATION_ID = "localNotificationId"
private const val EXTRA_TITLE = "title"
private const val EXTRA_MESSAGE = "message"
private const val NOTIFICATION_CHANNEL_ID = "channel_all_notifications"
private const val ACTION_SHOW_NOTIFICATION = "com.kmp.talktome.SHOW_NOTIFICATION"

class AndroidLocalNotificationService(
    private val context: Context,
) : LocalNotificationService {

    companion object {
        private const val LOG_TAG = "AndroidNotificationService"
    }

    private val notificationManager = NotificationManagerCompat.from(context)
    private val alarmManager = context.getSystemService<AlarmManager>()

    init {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "All notifications", IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    private fun getRelevantAlarmPermission(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return Manifest.permission.USE_EXACT_ALARM

        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.S..<Build.VERSION_CODES.TIRAMISU)
            return Manifest.permission.SCHEDULE_EXACT_ALARM

        return null
    }

    override suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        val permissions = listOfNotNull(Manifest.permission.POST_NOTIFICATIONS, getRelevantAlarmPermission())

        val permissionHandler = KoinPlatform.getKoin().getOrNull<PermissionHandler>()
        return permissionHandler?.requestPermissions(permissions.toTypedArray()) ?: false
    }

    override fun post(
        localNotificationId: LocalNotificationId,
        title: String,
        message: String,
        time: LocalDateTime?,
    ) {
        Napier.d("Posting notification: $localNotificationId, $title at $time", tag = LOG_TAG)

        if (time != null) {
            scheduleNotification(
                title = title,
                message = message,
                localNotificationId = localNotificationId,
                time = time,
            )
        } else {
            showNotification(
                title = title,
                message = message,
                localNotificationId = localNotificationId,
            )
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    @OptIn(ExperimentalTime::class)
    private fun scheduleNotification(
        title: String,
        message: String,
        localNotificationId: LocalNotificationId,
        time: LocalDateTime
    ) {
        alarmManager ?: return

        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            .setAction(ACTION_SHOW_NOTIFICATION)
            .putExtra(EXTRA_TITLE, title)
            .putExtra(EXTRA_MESSAGE, message)
            .putExtra(EXTRA_LOCAL_NOTIFICATION_ID, localNotificationId.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            localNotificationId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = getNotificationTime(time).toInstant(TIME_ZONE)
        val triggerAtMillis = triggerTime.toEpochMilliseconds()

        val alarmPermission = getRelevantAlarmPermission()
        if (alarmPermission != null &&
            ContextCompat.checkSelfPermission(context, alarmPermission) != PackageManager.PERMISSION_GRANTED
        ) {
            Napier.d("No $alarmPermission permission to schedule notification $localNotificationId", tag = LOG_TAG)

            return
        }

        Napier.d("Setting alarm for notification $localNotificationId, $triggerTime ($triggerAtMillis)", tag = LOG_TAG)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    private fun showNotification(
        title: String,
        message: String,
        localNotificationId: LocalNotificationId,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Napier.d("No permission to show notification $localNotificationId", tag = LOG_TAG)
            return
        }

        val mainActivityIntent = Intent(context, Class.forName("org.jetbrains.kotlinconf.android.MainActivity"))
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(EXTRA_LOCAL_NOTIFICATION_ID, localNotificationId.toString())
        val pendingIntent = PendingIntent.getActivity(
            context,
            localNotificationId.hashCode(),
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        Napier.d("Showing notification: $localNotificationId, $notification", tag = LOG_TAG)

        notificationManager.notify(localNotificationId.hashCode(), notification)
    }

    override fun cancel(localNotificationId: LocalNotificationId) {
        Napier.d("Canceling notification: $localNotificationId", tag = LOG_TAG)


        // Cancel the notification if it's currently shown
        notificationManager.cancel(localNotificationId.hashCode())
        Napier.d("Canceled notification: $localNotificationId", tag = LOG_TAG)

        // Cancel any pending alarms for this notification
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent: PendingIntent? = PendingIntent.getBroadcast(
            context,
            localNotificationId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let { alarmManager?.cancel(it) }
        Napier.d("Canceled scheduled notification: $localNotificationId", tag = LOG_TAG)
    }
}

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHOW_NOTIFICATION) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        val notificationId = intent.getStringExtra(EXTRA_LOCAL_NOTIFICATION_ID) ?: return

        val localNotificationService = KoinPlatform.getKoin().get<LocalNotificationService>()
        localNotificationService.post(
            title = title,
            message = message,
            localNotificationId = LocalNotificationId.parse(notificationId) ?: return,
        )
    }
}
