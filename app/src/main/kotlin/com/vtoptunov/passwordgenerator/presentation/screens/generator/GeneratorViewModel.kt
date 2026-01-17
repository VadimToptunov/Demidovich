package com.vtoptunov.passwordgenerator.presentation.screens.generator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle
import com.vtoptunov.passwordgenerator.domain.repository.PasswordRepository
import com.vtoptunov.passwordgenerator.domain.usecase.password.GeneratePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToLong

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val generatePasswordUseCase: GeneratePasswordUseCase,
    private val passwordRepository: PasswordRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(GeneratorState())
    val state: StateFlow<GeneratorState> = _state.asStateFlow()
    
    private var crackingJob: Job? = null
    
    init {
        generatePassword()
    }
    
    fun onEvent(event: GeneratorEvent) {
        when (event) {
            is GeneratorEvent.StyleSelected -> {
                _state.update { it.copy(selectedStyle = event.style) }
                generatePassword()
            }
            is GeneratorEvent.LengthChanged -> {
                _state.update { it.copy(passwordLength = event.length) }
                generatePassword()
            }
            is GeneratorEvent.OptionToggled -> {
                _state.update { current ->
                    when (event.option) {
                        PasswordOption.UPPERCASE -> current.copy(includeUppercase = !current.includeUppercase)
                        PasswordOption.LOWERCASE -> current.copy(includeLowercase = !current.includeLowercase)
                        PasswordOption.NUMBERS -> current.copy(includeNumbers = !current.includeNumbers)
                        PasswordOption.SYMBOLS -> current.copy(includeSymbols = !current.includeSymbols)
                    }
                }
                generatePassword()
            }
            GeneratorEvent.GeneratePassword -> generatePassword()
            GeneratorEvent.SavePassword -> savePassword()
            GeneratorEvent.CopyToClipboard -> copyToClipboard()
            is GeneratorEvent.CategorySelected -> {
                _state.update { it.copy(selectedCategory = event.category) }
            }
            GeneratorEvent.ToggleStyle -> {
                _state.update { it.copy(styleExpanded = !it.styleExpanded) }
            }
            GeneratorEvent.ToggleSettings -> {
                _state.update { it.copy(settingsExpanded = !it.settingsExpanded) }
            }
            GeneratorEvent.ToggleCategory -> {
                _state.update { it.copy(categoryExpanded = !it.categoryExpanded) }
            }
        }
    }
    
    private fun generatePassword() {
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true) }
            
            val result = generatePasswordUseCase(
                style = _state.value.selectedStyle,
                length = _state.value.passwordLength,
                includeUppercase = _state.value.includeUppercase,
                includeLowercase = _state.value.includeLowercase,
                includeNumbers = _state.value.includeNumbers,
                includeSymbols = _state.value.includeSymbols
            )
            
            _state.update { it.copy(generatedPassword = result, isGenerating = false) }
            
            startCrackingSimulation(result.password, result.entropy)
        }
    }
    
    private fun startCrackingSimulation(password: String, entropy: Double) {
        crackingJob?.cancel()
        
        if (password.isEmpty()) {
            _state.update { it.copy(crackingSimulation = null) }
            return
        }
        
        crackingJob = viewModelScope.launch {
            val durationMs = when {
                entropy < 28 -> 500L
                entropy < 36 -> 1500L
                entropy < 60 -> 3000L
                else -> 5000L
            }
            
            val stepMs = durationMs / password.length
            val attemptsPerChar = (2.0.pow(entropy / password.length) / 1000).roundToLong()
            
            var cracked = ""
            var attempts = 0L
            var timeMs = 0L
            
            password.forEachIndexed { index, char ->
                delay(stepMs)
                cracked += char
                attempts += attemptsPerChar
                timeMs += stepMs
                
                _state.update {
                    it.copy(
                        crackingSimulation = CrackingSimulationState(
                            crackedChars = cracked,
                            progress = (index + 1f) / password.length,
                            attempts = attempts,
                            timeElapsedMs = timeMs
                        )
                    )
                }
            }
        }
    }
    
    private fun savePassword() {
        val password = _state.value.generatedPassword?.password ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            passwordRepository.insertPassword(
                Password(
                    password = password,
                    category = _state.value.selectedCategory // USE selected category
                )
            )
            _state.update { it.copy(isSaving = false, showSaveSuccess = true) }
            delay(2000)
            _state.update { it.copy(showSaveSuccess = false) }
        }
    }
    
    private fun copyToClipboard() {
        val password = _state.value.generatedPassword?.password ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("password", password))
        
        // Auto-clear after 30s
        viewModelScope.launch {
            delay(30000)
            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
        }
    }
}

