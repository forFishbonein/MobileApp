package com.example.tutoring.ui.navigation.student

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tutoring.ui.screens.student.CoursesScreen
import com.example.tutoring.ui.screens.student.HomeScreen
import com.example.tutoring.ui.screens.student.LessonsScreen
import com.example.tutoring.ui.screens.student.ProfileScreen

@Composable
fun StudentNavHost(navController: NavHostController,onLoginOut: () -> Unit,) {
    NavHost(
        navController = navController,
        startDestination = StudentNavRoutes.Home.route
    ) {
        composable(StudentNavRoutes.Home.route) { HomeScreen() }
        composable(StudentNavRoutes.Courses.route) { CoursesScreen(navController) }
        composable(StudentNavRoutes.Profile.route) { ProfileScreen(onLoginOut) }
        composable(
            route = StudentNavRoutes.Lessons.route,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId")
            // 调用 LessonsScreen(courseId)
            LessonsScreen(courseId)
        }

    }
}
