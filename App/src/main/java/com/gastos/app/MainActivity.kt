package com.gastos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.gastos.app.ui.navigation.AppNavigation
import com.gastos.app.ui.navigation.Screen
import com.gastos.app.ui.theme.GastosAppTheme
import com.gastos.app.viewmodel.AuthViewModel
import com.gastos.app.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GastosAppTheme {
                val navController = rememberNavController()
                
                // Inicializar ViewModels
                val authViewModel = remember { AuthViewModel(applicationContext) }
                val expenseViewModel = remember { ExpenseViewModel() }
                
                // Determinar la pantalla inicial según el estado de autenticación
                val startDestination = if (authViewModel.isUserLoggedIn()) {
                    Screen.Home.route
                } else {
                    Screen.Login.route
                }
                
                AppNavigation(
                    navController = navController,
                    authViewModel = authViewModel,
                    expenseViewModel = expenseViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}