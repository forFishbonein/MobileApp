package com.example.tutoring.ui.navigation.tutor

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tutoring.ui.screens.tutor.LessonsScreen
import com.example.tutoring.ui.screens.tutor.ApplicationScreen
import com.example.tutoring.ui.screens.tutor.CoursesScreen
import com.example.tutoring.ui.screens.tutor.HomeScreen
import com.example.tutoring.ui.screens.tutor.ProfileScreen
import com.example.tutoring.ui.screens.tutor.AddLessonScreen

@Composable
fun TutorNavHost(navController: NavHostController,onLoginOut: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = TutorNavRoutes.Home.route
    ) {
        composable(TutorNavRoutes.Home.route) { HomeScreen() }
        composable(TutorNavRoutes.Application.route) { ApplicationScreen() }
        composable(TutorNavRoutes.Courses.route) { CoursesScreen(navController) }
        composable(TutorNavRoutes.Profile.route) { ProfileScreen(onLoginOut) }
        composable(
            route = TutorNavRoutes.Lessons.route,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId")
            // 调用 LessonsScreen(courseId)
            LessonsScreen(courseId, navController)
        }
        composable(TutorNavRoutes.AddLesson.route) { AddLessonScreen(navController) }
    }
}
