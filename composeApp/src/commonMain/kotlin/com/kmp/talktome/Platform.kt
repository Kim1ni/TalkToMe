package com.kmp.talktome

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform