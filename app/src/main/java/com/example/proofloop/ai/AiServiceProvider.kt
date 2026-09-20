package com.example.proofloop.ai

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AiServiceProvider {
    private val _isDemoMode = MutableStateFlow(true)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val demoService = DemoAiService()
    private var geminiService = GeminiService()

    fun setDemoMode(enabled: Boolean) {
        _isDemoMode.value = enabled
    }

    fun setApiKey(key: String) {
        _customApiKey.value = key
        geminiService = GeminiService(customApiKey = key)
        if (key.isNotBlank()) {
            _isDemoMode.value = false
        }
    }

    fun getService(): AiService {
        return if (_isDemoMode.value) {
            demoService
        } else {
            geminiService
        }
    }
}
