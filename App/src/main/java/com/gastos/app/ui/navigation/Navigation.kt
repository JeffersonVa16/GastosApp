package com.gastos.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gastos.app.ui.screens.AddEditExpenseScreen
import com.gastos.app.ui.screens.HomeScreen
import com.gastos.app.ui.screens.LoginScreen
import com.gastos.app.ui.screens.ProfileScreen
import com.gastos.app.viewmodel.AuthViewModel
import com.gastos.app.viewmodel.ExpenseViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddExpense : Screen("add_expense")
    object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: String) = "edit_expense/$expenseId"
    }
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                authViewModel = authViewModel,
                expenseViewModel = expenseViewModel,
                onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onExpenseClick = { expense ->
                    navController.navigate(Screen.EditExpense.createRoute(expense.id))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.AddExpense.route) {
            AddEditExpenseScreen(
                expenseViewModel = expenseViewModel,
                expense = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")
            val expenses by expenseViewModel.expenses.collectAsState()
            val expense = expenses.find { it.id == expenseId }
            
            AddEditExpenseScreen(
                expenseViewModel = expenseViewModel,
                expense = expense,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onAccountDeleted = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

