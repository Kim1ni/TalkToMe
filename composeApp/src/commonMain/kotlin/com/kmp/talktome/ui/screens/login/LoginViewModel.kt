package com.kmp.talktome.ui.screens.login

import androidx.lifecycle.ViewModel
import com.kmp.talktome.domain.usecase.login.GetCurrentUserUseCase
import com.kmp.talktome.domain.usecase.login.SignInAnonymouslyUseCase
import com.kmp.talktome.domain.usecase.login.SignInWithGoogleUseCase
import com.kmp.talktome.domain.util.onError
import com.kmp.talktome.domain.util.onSuccess
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val viewModelScope: CoroutineScope
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _state.update { it.copy(isAuthenticated = user != null) }
            }
        }
    }

    fun signInWithGoogle(user: FirebaseUser) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isGoogleLoading = true,
                    errorMessage = null,
                    showDomainHelp = false
                )
            }
            val idToken = user.getIdToken(true)

            if (idToken == null) {
                _state.update {
                    it.copy(
                        isGoogleLoading = false,
                        errorMessage = "Failed to get authentication token"
                    )
                }
                return@launch
            } else {
                signInWithGoogleUseCase(idToken)
                    .onSuccess { _ ->
                        _state.update {
                            it.copy(
                                isGoogleLoading = false,
                                isAuthenticated = true
                            )
                        }
                    }
                    .onError { exception ->
                        handleAuthError(exception)
                        _state.update { it.copy(isGoogleLoading = false) }
                    }
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isGuestLoading = true,
                    errorMessage = null,
                    showDomainHelp = false
                )
            }

            signInAnonymouslyUseCase()
                .onSuccess { _ ->
                    _state.update {
                        it.copy(
                            isGuestLoading = false,
                            isAuthenticated = true
                        )
                    }
                }
                .onError { exception ->
                    handleAuthError(exception)
                    _state.update { it.copy(isGuestLoading = false) }
                }
        }
    }

    fun handleGoogleSignInFailure(error: Exception) {
        val errorMessage = when {
            error.message?.contains("network error", ignoreCase = true) == true ->
                "No internet connection"
            else -> error.message ?: "Unknown error"
        }
        _state.update { it.copy(errorMessage = errorMessage) }
    }

    fun dismissError() {
        _state.update {
            it.copy(
                errorMessage = null,
                showDomainHelp = false
            )
        }
    }

    private fun handleAuthError(exception: Exception) {
        val message = exception.message ?: "Unknown error"

        when {
            message.contains("unauthorized-domain", ignoreCase = true) -> {
                _state.update {
                    it.copy(
                        showDomainHelp = true,
                        errorMessage = "Domain not authorized."
                    )
                }
            }
            message.contains("popup-closed-by-user", ignoreCase = true) -> {
                _state.update { it.copy(errorMessage = null) }
            }
            else -> {
                _state.update {
                    it.copy(errorMessage = message)
                }
            }
        }
    }
}