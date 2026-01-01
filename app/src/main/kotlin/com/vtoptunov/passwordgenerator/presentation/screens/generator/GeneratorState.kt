package com.vtoptunov.passwordgenerator.presentation.screens.generator

import com.vtoptunov.passwordgenerator.domain.model.PasswordGenerationResult
import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle

data class GeneratorState(
    val generatedPassword: PasswordGenerationResult? = null,
    val selectedStyle: PasswordStyle = PasswordStyle.Random,
    val passwordLength: Int = 16,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = true,
    val isGenerating: Boolean = false,
    val isSaving: Boolean = false,
    val showSaveSuccess: Boolean = false,
    val crackingSimulation: CrackingSimulationState? = null,
    val copiedToClipboard: Boolean = false
)

data class CrackingSimulationState(
    val crackedChars: String = "",
    val progress: Float = 0f,
    val attempts: Long = 0,
    val timeElapsedMs: Long = 0,
    val isComplete: Boolean = false
)

sealed class GeneratorEvent {
    data class StyleSelected(val style: PasswordStyle) : GeneratorEvent()
    data class LengthChanged(val length: Int) : GeneratorEvent()
    data class OptionToggled(val option: PasswordOption) : GeneratorEvent()
    object GeneratePassword : GeneratorEvent()
    object SavePassword : GeneratorEvent()
    object CopyToClipboard : GeneratorEvent()
    object DismissSaveSuccess : GeneratorEvent()
    object DismissCopiedMessage : GeneratorEvent()
}

enum class PasswordOption {
    UPPERCASE,
    LOWERCASE,
    NUMBERS,
    SYMBOLS
}

