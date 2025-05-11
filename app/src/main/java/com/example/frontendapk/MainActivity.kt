package com.example.frontendapk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontendapk.ui.theme.FrontendAPKTheme
import com.example.frontendapk.view.RegisterScreen // Importa RegisterScreen aquí

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontendAPKTheme {
                val navController = rememberNavController() // Crear controlador de navegación

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Pasa innerPadding a los composables
                    NavHost(navController = navController, startDestination = "register") {
                        composable("register") {
                            RegisterScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}