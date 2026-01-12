package com.kmp.talktome.di

import com.kmp.talktome.data.repository.FirebaseAuthRepositoryImpl
import com.kmp.talktome.data.repository.SettingsRepositoryImpl
import com.kmp.talktome.data.repository.PersonaRepositoryImpl
import com.kmp.talktome.data.repository.PreferencesRepositoryImpl
import com.kmp.talktome.data.repository.SessionRepositoryImpl
import com.kmp.talktome.data.repository.TodoRepositoryImpl
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PersonaRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.repository.SettingsRepository
import com.kmp.talktome.domain.repository.TodoRepository
import com.russhwolf.settings.Settings
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.koin.dsl.module

val dataModule = module {
    // Firebase
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }

    // Multiplatform settings
    single { Settings }

    // Implementations
    single<AuthRepository> { FirebaseAuthRepositoryImpl( get(), get() ) }
    single<SessionRepository> { SessionRepositoryImpl(get(), get()) }
    single<TodoRepository> { TodoRepositoryImpl(get()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
    single<PersonaRepository> { PersonaRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

}