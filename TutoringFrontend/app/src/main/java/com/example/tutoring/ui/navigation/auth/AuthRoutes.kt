package com.example.tutoring.ui.navigation.auth

sealed class AuthRoutes(val route: String) {
    object Login : AuthRoutes("login")
    object Register : AuthRoutes("register")
}
