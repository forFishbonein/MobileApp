package com.example.tutoring.ui.navigation.student

sealed class StudentNavRoutes(val route: String) {
    data object Home : StudentNavRoutes("student_home")
    data object Courses : StudentNavRoutes("student_courses")
    data object Profile : StudentNavRoutes("student_profile")
    data object Lessons : StudentNavRoutes("student_lessons/{courseId}")
}

