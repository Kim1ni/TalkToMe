package com.kmp.talktome.di

import com.kmp.talktome.data.live.GeminiAnalysisServiceImpl
import com.kmp.talktome.domain.live.GeminiAnalysisService
import com.kmp.talktome.domain.usecase.home.CalculateStreakUseCase
import com.kmp.talktome.domain.usecase.home.DeleteTodoUseCase
import com.kmp.talktome.domain.usecase.home.GetDashboardDataUseCase
import com.kmp.talktome.domain.usecase.home.ToggleTodoUseCase
import com.kmp.talktome.domain.usecase.login.GetCurrentUserUseCase
import com.kmp.talktome.domain.usecase.login.SignInAnonymouslyUseCase
import com.kmp.talktome.domain.usecase.login.SignInWithGoogleUseCase
import com.kmp.talktome.domain.usecase.session.AnalyzeSessionUseCase
import com.kmp.talktome.domain.usecase.session.EndSessionUseCase
import com.kmp.talktome.domain.usecase.session.SaveSessionUseCase
import com.kmp.talktome.domain.usecase.session.StartSessionUseCase
import org.koin.dsl.module

val domainModule = module {
    // Auth Use Cases
    factory { SignInWithGoogleUseCase(get()) }
    factory { SignInAnonymouslyUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    // Home Use Cases
    factory { CalculateStreakUseCase() }
    factory { GetDashboardDataUseCase(get(), get(), get()) }
    factory { DeleteTodoUseCase(get()) }
    factory { ToggleTodoUseCase(get()) }

    // Session Use Cases
    factory { AnalyzeSessionUseCase(get(), get()) }
    factory { StartSessionUseCase(get()) }
    factory { EndSessionUseCase(get()) }
    factory { SaveSessionUseCase(get(), get()) }
    single<GeminiAnalysisService>{ GeminiAnalysisServiceImpl() }

}
