package com.kmp.talktome.di

import com.kmp.talktome.data.repository.FirebaseAuthRepositoryImpl
import com.kmp.talktome.data.repository.PreferencesRepositoryImpl
import com.kmp.talktome.data.repository.SessionRepositoryImpl
import com.kmp.talktome.data.repository.TodoRepositoryImpl
import com.kmp.talktome.domain.repository.AuthRepository
import com.kmp.talktome.domain.repository.PreferencesRepository
import com.kmp.talktome.domain.repository.SessionRepository
import com.kmp.talktome.domain.repository.TodoRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val dataModule = module {
    // Firebase
    single { Firebase.auth }
    single { Firebase.firestore }

    single<AuthRepository> {
        FirebaseAuthRepositoryImpl( get(), get() )
    }
    single<SessionRepository> {
        SessionRepositoryImpl()
    }
    single<TodoRepository> {
        TodoRepositoryImpl()
    }
    single<PreferencesRepository> {
        PreferencesRepositoryImpl()
    }
}