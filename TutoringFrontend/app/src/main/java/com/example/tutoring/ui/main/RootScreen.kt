package com.example.tutoring.ui.main

import androidx.compose.runtime.Composable
import com.example.tutoring.data.Role
import com.example.tutoring.ui.navigation.student.StudentMainScreen
import com.example.tutoring.ui.navigation.tutor.TutorMainScreen

@Composable
fun RootScreen(userRole: Role) {
    when (userRole) {
        Role.STUDENT -> StudentMainScreen()
        Role.TUTOR -> TutorMainScreen()
    }
}
