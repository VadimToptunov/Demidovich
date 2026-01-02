package com.vtoptunov.passwordgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vtoptunov.passwordgenerator.presentation.navigation.AppNavigation
import com.vtoptunov.passwordgenerator.presentation.theme.DeepSpace
import com.vtoptunov.passwordgenerator.presentation.theme.PasswordGeneratorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            PasswordGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepSpace
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

