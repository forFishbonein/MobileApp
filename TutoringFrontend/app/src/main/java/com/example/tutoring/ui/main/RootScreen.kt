package com.example.tutoring.ui.main

import androidx.compose.runtime.Composable
import com.example.tutoring.data.Role
import com.example.tutoring.ui.navigation.student.StudentMainScreen
import com.example.tutoring.ui.navigation.tutor.TutorMainScreen

@Composable
fun RootScreen(userRole: Role, onLoginOut: () -> Unit) {
    when (userRole) {
        Role.STUDENT -> StudentMainScreen(onLoginOut)
        Role.TUTOR -> TutorMainScreen(onLoginOut)
    }
}
