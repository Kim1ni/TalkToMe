package com.kmp.talktome.di

import com.kmp.talktome.ui.navigation.AuthViewModel
import com.kmp.talktome.ui.screens.history.HistoryViewModel
import com.kmp.talktome.ui.screens.home.HomeViewModel
import com.kmp.talktome.ui.screens.login.LoginViewModel
import com.kmp.talktome.ui.screens.persona.PersonaViewModel
import com.kmp.talktome.ui.screens.profile.ProfileViewModel
import com.kmp.talktome.ui.screens.session.SessionViewModel
import com.kmp.talktome.ui.screens.session_details.SessionDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
val ioDispatcher = Dispatchers.IO

val uiModule = module {
    single { viewModelScope }
    single { ioDispatcher }


    viewModelOf(::AuthViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SessionViewModel)
    viewModelOf(::SessionDetailsViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::PersonaViewModel)
}
