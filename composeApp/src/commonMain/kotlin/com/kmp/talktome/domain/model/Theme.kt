package com.kmp.talktome.domain.model

enum class Theme {
    LIGHT, DARK;

    companion object {
        fun fromString(value: String?): Theme {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: LIGHT
        }
    }
}