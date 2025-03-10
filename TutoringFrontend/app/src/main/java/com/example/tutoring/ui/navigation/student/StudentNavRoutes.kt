package com.example.tutoring.ui.navigation.student

sealed class StudentNavRoutes(val route: String) {
    object Home : StudentNavRoutes("student_home")
    object Courses : StudentNavRoutes("student_courses")
    object Profile : StudentNavRoutes("student_profile")
}

