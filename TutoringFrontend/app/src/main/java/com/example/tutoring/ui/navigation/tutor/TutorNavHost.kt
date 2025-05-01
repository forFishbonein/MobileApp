package com.example.tutoring.ui.navigation.tutor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tutoring.data.Lesson
import com.example.tutoring.ui.screens.tutor.LessonsScreen
import com.example.tutoring.ui.screens.tutor.ApplicationScreen
import com.example.tutoring.ui.screens.tutor.CoursesScreen
import com.example.tutoring.ui.screens.tutor.HomeScreen
import com.example.tutoring.ui.screens.tutor.ProfileScreen
import com.example.tutoring.ui.screens.tutor.AddLessonScreen
import com.example.tutoring.ui.screens.tutor.MeetingScreen
import com.example.tutoring.ui.screens.tutor.common.TutorCourseHost
import com.google.gson.Gson
import java.net.URLDecoder

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TutorNavHost(navController: NavHostController,onLoginOut: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = TutorNavRoutes.Home.route
    ) {
        composable(TutorNavRoutes.Home.route) { HomeScreen() }
        composable(TutorNavRoutes.Application.route) { ApplicationScreen(navController) }
        composable(TutorNavRoutes.Courses.route) { TutorCourseHost(navController,TutorNavRoutes.Courses.route) }
        composable(TutorNavRoutes.Meetings.route) { TutorCourseHost(navController,TutorNavRoutes.Meetings.route) }
        composable(TutorNavRoutes.Profile.route) { ProfileScreen(onLoginOut) }
        composable(
            route = TutorNavRoutes.Lessons.route,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId")
            LessonsScreen(courseId, navController)
        }
        composable(
            route = TutorNavRoutes.AddLesson.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.IntType
                    defaultValue = -1  // -1 indicates that the courseId is not passed
                },
                navArgument("lesson") {
                    type = NavType.StringType
                    defaultValue = ""  // An empty string indicates that no lesson object was passed
                }
            )
        ) { backStackEntry ->
            // Serialize back the Lesson object from the string
            val courseId = backStackEntry.arguments?.getInt("courseId")
            val encodedLesson = backStackEntry.arguments?.getString("lesson") ?: ""
            val lessonJson = URLDecoder.decode(encodedLesson, "UTF-8")
            val lesson: Lesson? = if (lessonJson.isNotBlank()) {
                Gson().fromJson(lessonJson, Lesson::class.java)
            } else null
            AddLessonScreen(
                navController = navController,
                courseId = courseId,
                lesson = lesson
            )
        }

    }
}
