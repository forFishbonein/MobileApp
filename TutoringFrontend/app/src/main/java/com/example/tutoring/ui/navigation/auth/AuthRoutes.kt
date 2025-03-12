package com.example.tutoring.ui.navigation.auth

sealed class AuthRoutes(val route: String) {
    data object Login : AuthRoutes("login")
    data object Register : AuthRoutes("register")
}
