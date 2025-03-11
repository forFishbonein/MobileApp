package com.example.tutoring.ui.navigation.auth

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutoring.data.Role
import com.example.tutoring.ui.screens.auth.LoginScreen
import com.example.tutoring.ui.screens.auth.RegisterScreen

@Composable
fun AuthNavHost(
    authNavController: NavHostController,
    onLoginSuccess: (Role) -> Unit,
    context: Context,
) {
    NavHost(
        navController = authNavController,
        startDestination = AuthRoutes.Login.route
    ) {
        composable(AuthRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    onLoginSuccess(role)
                },
                onNavigateToRegister = {
                    authNavController.navigate(AuthRoutes.Register.route)
                },
                context,
            )
        }
        composable(AuthRoutes.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    authNavController.popBackStack()
                }
            )
        }
    }
}
