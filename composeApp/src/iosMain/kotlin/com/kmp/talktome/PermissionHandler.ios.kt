package com.kmp.talktome

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.UserNotifications.*
import platform.AVFoundation.*
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted
import kotlin.coroutines.resume

class IOSPermissionHandler : PermissionHandler {
    private val _notificationPermissionState = MutableStateFlow(false)
    private val _microphonePermissionState = MutableStateFlow(false)
    private val _storagePermissionState = MutableStateFlow(true) // iOS apps have access to their sandbox

    init {
        checkNotificationPermission()
        checkMicrophonePermission()
    }

    override fun observeNotificationPermission(): Flow<Boolean> = _notificationPermissionState
    override fun observeMicrophonePermission(): Flow<Boolean> = _microphonePermissionState
    override fun observeStoragePermission(): Flow<Boolean> = _storagePermissionState

    override fun checkNotificationPermission(): Boolean {
        UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
            val granted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
            _notificationPermissionState.value = granted
        }
        return _notificationPermissionState.value
    }

    override suspend fun requestNotificationPermission(): Boolean = suspendCancellableCoroutine { continuation ->
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            ) { granted, _ ->
                _notificationPermissionState.value = granted
                continuation.resume(granted)
            }
    }

    override fun checkMicrophonePermission(): Boolean {
        val status = AVAudioSession.sharedInstance().recordPermission()
        val granted = status == AVAudioSessionRecordPermissionGranted
        _microphonePermissionState.value = granted
        return granted
    }

    override suspend fun requestMicrophonePermission(): Boolean = suspendCancellableCoroutine { continuation ->
        AVAudioSession.sharedInstance().requestRecordPermission { granted ->
            _microphonePermissionState.value = granted
            continuation.resume(granted)
        }
    }

    override fun checkStoragePermission(): Boolean = true

    override suspend fun requestStoragePermission(): Boolean = true
}

actual fun getPermissionHandler(): PermissionHandler = IOSPermissionHandler()
