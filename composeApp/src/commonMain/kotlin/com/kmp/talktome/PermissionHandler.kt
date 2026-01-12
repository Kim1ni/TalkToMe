package com.kmp.talktome

import kotlinx.coroutines.flow.Flow

interface PermissionHandler {
    // Notifications
    suspend fun requestNotificationPermission(): Boolean
    fun observeNotificationPermission(): Flow<Boolean>
    fun checkNotificationPermission(): Boolean

    // Microphone
    suspend fun requestMicrophonePermission(): Boolean
    fun observeMicrophonePermission(): Flow<Boolean>
    fun checkMicrophonePermission(): Boolean

    // Storage
    suspend fun requestStoragePermission(): Boolean
    fun observeStoragePermission(): Flow<Boolean>
    fun checkStoragePermission(): Boolean
}

expect fun getPermissionHandler(): PermissionHandler