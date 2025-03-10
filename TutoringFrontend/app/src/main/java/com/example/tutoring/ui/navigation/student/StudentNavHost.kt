package com.example.tutoring.ui.navigation.student

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tutoring.ui.screens.student.CoursesScreen
import com.example.tutoring.ui.screens.student.HomeScreen
import com.example.tutoring.ui.screens.student.ProfileScreen

@Composable
fun StudentNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = StudentNavRoutes.Home.route
    ) {
        composable(StudentNavRoutes.Home.route) { HomeScreen() }
        composable(StudentNavRoutes.Courses.route) { CoursesScreen() }
        composable(StudentNavRoutes.Profile.route) { ProfileScreen() }
    }
}
