package com.example.tutoring.ui.navigation.tutor

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tutoring.ui.screens.tutor.ApplicationScreen
import com.example.tutoring.ui.screens.tutor.CoursesScreen
import com.example.tutoring.ui.screens.tutor.HomeScreen
import com.example.tutoring.ui.screens.tutor.ProfileScreen

@Composable
fun TutorNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TutorNavRoutes.Home.route
    ) {
        composable(TutorNavRoutes.Home.route) { HomeScreen() }
        composable(TutorNavRoutes.Application.route) { ApplicationScreen() }
        composable(TutorNavRoutes.Courses.route) { CoursesScreen() }
        composable(TutorNavRoutes.Profile.route) { ProfileScreen() }
    }
}
