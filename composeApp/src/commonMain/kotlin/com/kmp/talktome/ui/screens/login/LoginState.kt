package com.kmp.talktome.ui.screens.login

data class LoginState(
    val isAuthenticated: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val isGuestLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDomainHelp: Boolean = false,
    val domainCopied: Boolean = false
)
