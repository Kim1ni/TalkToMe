package com.kmp.talktome.di

import com.kmp.talktome.domain.model.SessionAnalysis
import com.kmp.talktome.domain.permissions.PermissionManager
import com.kmp.talktome.domain.usecase.home.CalculateStreakUseCase
import com.kmp.talktome.domain.usecase.todo.DeleteTodoUseCase
import com.kmp.talktome.domain.usecase.home.GetDashboardDataUseCase
import com.kmp.talktome.domain.usecase.todo.ToggleTodoUseCase
import com.kmp.talktome.domain.usecase.login.GetCurrentUserUseCase
import com.kmp.talktome.domain.usecase.login.SignInAnonymouslyUseCase
import com.kmp.talktome.domain.usecase.login.SignInWithGoogleUseCase
import com.kmp.talktome.domain.usecase.session.EndSessionUseCase
import com.kmp.talktome.domain.usecase.session.AnalyzeSessionUseCase
import com.kmp.talktome.domain.usecase.session.SaveSessionUseCase
import com.kmp.talktome.domain.usecase.session.StartSessionUseCase
import com.kmp.talktome.domain.usecase.session.UpdateSessionAnalysisUseCase
import com.kmp.talktome.domain.usecase.todo.AddTodoReflectionUseCase
import com.kmp.talktome.domain.usecase.todo.CreateTodosFromAnalysisUseCase
import org.koin.dsl.module

val domainModule = module {
    // Auth Use Cases
    factory { SignInWithGoogleUseCase(get()) }
    factory { SignInAnonymouslyUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    // Home Use Cases
    factory { CalculateStreakUseCase() }
    factory {
        GetDashboardDataUseCase(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    // Session Use Cases
    factory { StartSessionUseCase(get()) }
    factory { EndSessionUseCase(get()) }
    factory { SaveSessionUseCase(get(), get()) }
    single { AnalyzeSessionUseCase(SessionAnalysis.getSchema()) }
    factory { UpdateSessionAnalysisUseCase(get()) }

    // To-Dos Use cases
    factory { CreateTodosFromAnalysisUseCase(get()) }
    factory { DeleteTodoUseCase(get()) }
    factory { ToggleTodoUseCase(get()) }
    factory { AddTodoReflectionUseCase(get()) }


    single<PermissionManager> { PermissionManager(get()) }
}
