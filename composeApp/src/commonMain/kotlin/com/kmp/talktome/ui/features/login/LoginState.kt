package com.kmp.talktome.ui.features.login

data class LoginState(
    val isAuthenticated: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val isGuestLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDomainHelp: Boolean = false,
    val currentDomain: String = "theravoice-app.firebaseapp.com",
    val projectId: String = "theravoice-app",
    val domainCopied: Boolean = false
) {
    val isLoading: Boolean
        get() = isGoogleLoading || isGuestLoading
}
