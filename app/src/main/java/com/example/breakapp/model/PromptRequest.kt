package com.example.breakapp.model

data class PromptRequest(
    val prompt: String,
    val userId: String? = null
)
