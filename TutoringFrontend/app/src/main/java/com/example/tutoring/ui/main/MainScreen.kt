package com.example.tutoring.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.example.tutoring.data.Role
import com.example.tutoring.ui.navigation.student.StudentMainScreen
import com.example.tutoring.ui.navigation.tutor.TutorMainScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(userRole: Role, onLoginOut: () -> Unit) {
    when (userRole) {
        Role.STUDENT -> StudentMainScreen(onLoginOut)
        Role.TUTOR -> TutorMainScreen(onLoginOut)
    }
}
