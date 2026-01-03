package com.kmp.talktome

import androidx.compose.ui.window.ComposeUIViewController
import com.kmp.talktome.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure =  { initKoin() }
) { App() }