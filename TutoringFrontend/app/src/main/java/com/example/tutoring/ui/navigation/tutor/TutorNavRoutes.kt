package com.example.tutoring.ui.navigation.tutor

sealed class TutorNavRoutes(val route: String) {
    object Home : TutorNavRoutes("tutor_home")
    object Application : TutorNavRoutes("tutor_application")
    object Courses : TutorNavRoutes("tutor_courses")
    object Profile : TutorNavRoutes("tutor_profile")
}
