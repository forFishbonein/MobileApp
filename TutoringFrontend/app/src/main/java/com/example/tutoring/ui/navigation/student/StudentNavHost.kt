package com.example.tutoring.ui.navigation.student

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.tutoring.ui.screens.student.common.StudentCourseHost

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentNavHost(navController: NavHostController,onLoginOut: () -> Unit,) {
    NavHost(
        navController = navController,
        startDestination = StudentNavRoutes.Home.route
    ) {
        composable(StudentNavRoutes.Home.route) { HomeScreen() }
        composable(StudentNavRoutes.Courses.route) { StudentCourseHost(navController, StudentNavRoutes.Courses.route) }
        composable(StudentNavRoutes.Meetings.route) { StudentCourseHost(navController, StudentNavRoutes.Meetings.route) }
        composable(StudentNavRoutes.Profile.route) { ProfileScreen(onLoginOut) }
        composable(
            route = StudentNavRoutes.Lessons.route,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId")
            LessonsScreen(courseId)
        }

    }
}
