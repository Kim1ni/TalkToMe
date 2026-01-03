package com.kmp.talktome.di

import com.kmp.talktome.ui.features.history.HistoryViewModel
import com.kmp.talktome.ui.features.home.HomeViewModel
import com.kmp.talktome.ui.features.login.LoginViewModel
import com.kmp.talktome.ui.features.profile.ProfileViewModel
import com.kmp.talktome.ui.features.session.SessionViewModel
import com.kmp.talktome.ui.features.session_details.SessionDetailsViewModel
import com.kmp.talktome.ui.navigation.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


val uiModule = module {

    factory {
        AuthViewModel(
            getCurrentUserUseCase = get(),
        )
    }

    factory {
        LoginViewModel(
            signInWithGoogleUseCase = get(),
            signInAnonymouslyUseCase = get(),
            getCurrentUserUseCase = get(),
            viewModelScope = viewModelScope
        )
    }

    factory {
        HomeViewModel(
            getDashboardDataUseCase = get(),
            toggleTodoUseCase = get(),
            deleteTodoUseCase = get(),
            calculateStreakUseCase = get(),
            authRepository = get(),
            viewModelScope = viewModelScope
        )
    }
    
    factory {
        SessionViewModel(
            startSessionUseCase = get(),
            endSessionUseCase = get(),
            saveSessionUseCase = get(),
            analyzeSessionUseCase = get(),
            preferencesRepository = get(),
            authRepository = get(),
            viewModelScope = viewModelScope
        )
    }

    factory {
        SessionDetailsViewModel(
            sessionRepository = get(),
            audioPlayer = get(),
            ttsEngine = get(),
            ioDispatcher = Dispatchers.IO,
        )
    }

    factory {
        HistoryViewModel(
            getCurrentUserUseCase = get(),
            sessionRepository = get(),
        )
    }

    factory {
        ProfileViewModel(
            authRepository = get(),
            preferencesRepository = get(),
        )
    }
}
