package com.kmp.talktome.domain.permissions

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager(
    val controller: PermissionsController
) {
    private val _notificationState = MutableStateFlow(PermissionState.NotDetermined)
    val notificationState: StateFlow<PermissionState> = _notificationState.asStateFlow()

    private val _microphoneState = MutableStateFlow(PermissionState.NotDetermined)
    val microphoneState: StateFlow<PermissionState> = _microphoneState.asStateFlow()

    private val _storageState = MutableStateFlow(PermissionState.NotDetermined)
    val storageState: StateFlow<PermissionState> = _storageState.asStateFlow()

    /**
     * Reusable internal function to handle the permission request logic
     * and the associated exception mapping.
     */
    private suspend fun requestPermissionInternal(
        permission: Permission,
        stateFlow: MutableStateFlow<PermissionState>
    ): PermissionState {
        return try {
            controller.providePermission(permission)
            val newState = controller.getPermissionState(permission)
            stateFlow.value = newState
            newState
        } catch (e: DeniedAlwaysException) {
            stateFlow.value = PermissionState.DeniedAlways
            PermissionState.DeniedAlways
        } catch (e: DeniedException) {
            stateFlow.value = PermissionState.Denied
            PermissionState.Denied
        } catch (e: RequestCanceledException) {
            // Keep current state or set to NotDetermined if canceled
            stateFlow.value
        } catch (e: Exception) {
            e.printStackTrace()
            stateFlow.value = PermissionState.Denied
            PermissionState.Denied
        }
    }

    // --- Public API ---

    suspend fun checkNotificationPermission(): PermissionState {
        return controller.getPermissionState(Permission.REMOTE_NOTIFICATION).also {
            _notificationState.value = it
        }
    }

    suspend fun requestNotificationPermission(): PermissionState {
        return requestPermissionInternal(Permission.REMOTE_NOTIFICATION, _notificationState)
    }

    suspend fun checkMicrophonePermission(): PermissionState {
        return controller.getPermissionState(Permission.RECORD_AUDIO).also {
            _microphoneState.value = it
        }
    }

    suspend fun requestMicrophonePermission(): PermissionState {
        return requestPermissionInternal(Permission.RECORD_AUDIO, _microphoneState)
    }

    suspend fun checkStoragePermission(): PermissionState {
        return controller.getPermissionState(Permission.STORAGE).also {
            _storageState.value = it
        }
    }

    suspend fun requestStoragePermission(): PermissionState {
        return requestPermissionInternal(Permission.STORAGE, _storageState)
    }

    fun openAppSettings() {
        controller.openAppSettings()
    }

    fun PermissionState.isGranted(): Boolean = this == PermissionState.Granted
    fun PermissionState.shouldShowRationale(): Boolean = this == PermissionState.DeniedAlways
}
