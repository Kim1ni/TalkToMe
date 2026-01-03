package com.kmp.talktome.domain.model

enum class TodoCategory {
    COGNITIVE, BEHAVIOURAL, SOCIAL;

    companion object {
        fun fromString(value: String?): TodoCategory? {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}