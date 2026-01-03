package com.kmp.talktome.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

expect fun platformModule(): Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration.invoke(this)
        modules(
            modules = listOf(
                platformModule(),
                dataModule,
                domainModule,
                uiModule
            )
        )
    }
}
