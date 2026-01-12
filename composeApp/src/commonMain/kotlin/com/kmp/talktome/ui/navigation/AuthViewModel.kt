package com.kmp.talktome.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmp.talktome.domain.usecase.login.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel()    {

    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.LOADING)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                if (user != null) {
                    _state.value = AuthState.AUTHENTICATED
                } else {
                    _state.value = AuthState.UNAUTHENTICATED
                }
            }
        }
    }
}

enum class AuthState {
    AUTHENTICATED,
    UNAUTHENTICATED,
    LOADING
}