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
import kotlin.math.roundToLong
import kotlin.random.Random

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val generatePasswordUseCase: GeneratePasswordUseCase,
    private val passwordRepository: PasswordRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(GeneratorState())
    val state: StateFlow<GeneratorState> = _state.asStateFlow()
    
    private var crackingSimulationJob: Job? = null
    
    init {
        // Generate initial password
        generatePassword()
    }
    
    fun onEvent(event: GeneratorEvent) {
        when (event) {
            is GeneratorEvent.StyleSelected -> selectStyle(event.style)
            is GeneratorEvent.LengthChanged -> updateLength(event.length)
            is GeneratorEvent.OptionToggled -> toggleOption(event.option)
            GeneratorEvent.GeneratePassword -> generatePassword()
            GeneratorEvent.SavePassword -> savePassword()
            GeneratorEvent.CopyToClipboard -> copyToClipboard()
            GeneratorEvent.DismissSaveSuccess -> _state.update { it.copy(showSaveSuccess = false) }
            GeneratorEvent.DismissCopiedMessage -> _state.update { it.copy(copiedToClipboard = false) }
        }
    }
    
    private fun selectStyle(style: PasswordStyle) {
        _state.update { it.copy(selectedStyle = style) }
        generatePassword()
    }
    
    private fun updateLength(length: Int) {
        _state.update { it.copy(passwordLength = length) }
    }
    
    private fun toggleOption(option: PasswordOption) {
        _state.update { currentState ->
            when (option) {
                PasswordOption.UPPERCASE -> currentState.copy(includeUppercase = !currentState.includeUppercase)
                PasswordOption.LOWERCASE -> currentState.copy(includeLowercase = !currentState.includeLowercase)
                PasswordOption.NUMBERS -> currentState.copy(includeNumbers = !currentState.includeNumbers)
                PasswordOption.SYMBOLS -> currentState.copy(includeSymbols = !currentState.includeSymbols)
            }
        }
    }
    
    private fun generatePassword() {
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true) }
            
            try {
                val result = generatePasswordUseCase(
                    style = _state.value.selectedStyle,
                    length = _state.value.passwordLength,
                    includeUppercase = _state.value.includeUppercase,
                    includeLowercase = _state.value.includeLowercase,
                    includeNumbers = _state.value.includeNumbers,
                    includeSymbols = _state.value.includeSymbols
                )
                
                _state.update { it.copy(generatedPassword = result) }
                
                // Start cracking simulation
                startCrackingSimulation(result.password, result.entropy)
                
            } finally {
                _state.update { it.copy(isGenerating = false) }
            }
        }
    }
    
    private fun startCrackingSimulation(password: String, entropy: Double) {
        crackingSimulationJob?.cancel()
        
        crackingSimulationJob = viewModelScope.launch {
            // Calculate simulation speed based on entropy
            // Lower entropy = faster cracking simulation
            val totalDurationMs = when {
                entropy < 28 -> 500L  // Very weak - crack fast
                entropy < 36 -> 1500L // Weak
                entropy < 60 -> 3000L // Fair
                else -> 5000L          // Strong - crack slowly
            }
            
            val stepDelayMs = totalDurationMs / password.length
            val attemptsPerChar = (2.0.pow(entropy / password.length) / 1000).roundToLong()
            
            var crackedChars = ""
            var totalAttempts = 0L
            var timeElapsed = 0L
            
            password.forEachIndexed { index, char ->
                delay(stepDelayMs)
                crackedChars += char
                totalAttempts += attemptsPerChar
                timeElapsed += stepDelayMs
                
                _state.update {
                    it.copy(
                        crackingSimulation = CrackingSimulationState(
                            crackedChars = crackedChars,
                            progress = (index + 1).toFloat() / password.length,
                            attempts = totalAttempts,
                            timeElapsedMs = timeElapsed,
                            isComplete = index == password.length - 1
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
            
            try {
                val passwordModel = Password(
                    password = password,
                    createdAt = System.currentTimeMillis()
                )
                
                passwordRepository.insertPassword(passwordModel)
                
                _state.update { it.copy(showSaveSuccess = true) }
                
                // Auto dismiss after 2 seconds
                delay(2000)
                _state.update { it.copy(showSaveSuccess = false) }
                
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }
    
    private fun copyToClipboard() {
        val password = _state.value.generatedPassword?.password ?: return
        
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", password)
        clipboardManager.setPrimaryClip(clip)
        
        _state.update { it.copy(copiedToClipboard = true) }
        
        // Auto-clear clipboard after 30 seconds for security
        viewModelScope.launch {
            delay(30000)
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""))
        }
        
        // Auto dismiss message after 2 seconds
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(copiedToClipboard = false) }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        crackingSimulationJob?.cancel()
    }
}

private fun Double.pow(exponent: Double): Double = kotlin.math.pow(this, exponent)

