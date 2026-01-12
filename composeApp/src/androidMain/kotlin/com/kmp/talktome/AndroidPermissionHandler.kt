package com.kmp.talktome

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.kmp.talktome.domain.notifications.AndroidNotificationManager
import com.kmp.talktome.domain.notifications.NotificationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidPermissionHandler(
    private val context: Context
) : PermissionHandler {
    private val _notificationPermissionState = MutableStateFlow(checkNotificationPermission())
    private val _microphonePermissionState = MutableStateFlow(checkMicrophonePermission())
    private val _storagePermissionState = MutableStateFlow(checkStoragePermission())

    override fun observeNotificationPermission(): Flow<Boolean> = _notificationPermissionState.asStateFlow()
    override fun observeMicrophonePermission(): Flow<Boolean> = _microphonePermissionState.asStateFlow()
    override fun observeStoragePermission(): Flow<Boolean> = _storagePermissionState.asStateFlow()

    override fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override suspend fun requestNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        // Actual request happens in UI layer via Accompanist or ActivityResultLauncher
        return checkNotificationPermission()
    }

    override fun checkMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestMicrophonePermission(): Boolean {
        return checkMicrophonePermission()
    }

    override fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, we might need READ_MEDIA_AUDIO for this app's purpose
            // But if we stick to the manifest's READ_EXTERNAL_STORAGE:
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override suspend fun requestStoragePermission(): Boolean {
        return checkStoragePermission()
    }

    fun updatePermissionStates() {
        _notificationPermissionState.value = checkNotificationPermission()
        _microphonePermissionState.value = checkMicrophonePermission()
        _storagePermissionState.value = checkStoragePermission()
    }

    fun updateNotificationPermission(granted: Boolean) {
        _notificationPermissionState.value = granted
    }
}

private var appContext: Context? = null

fun initPermissionHandler(context: Context) {
    appContext = context
}

actual fun getPermissionHandler(): PermissionHandler {
    return AndroidPermissionHandler(appContext ?: throw IllegalStateException("Context not initialized"))
}