package com.example.tutoring.ui.screens.student.common

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.tutoring.ui.navigation.student.StudentNavRoutes
import com.example.tutoring.ui.screens.student.CoursesScreen
import com.example.tutoring.ui.screens.student.MeetingScreen
import com.example.tutoring.utils.ErrorNotifier

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentCourseHost(navController: NavHostController, currentRoute: String) {
    // 1) Save tutorIds
    var tutorIds by remember { mutableStateOf<List<Int>>(emptyList()) }
    // 2) Mark that the course loading is complete
    var coursesLoaded by remember { mutableStateOf(false) }

    Column {
        StudentCourseTopNav(currentRoute) { destination ->
//            if (destination == StudentNavRoutes.Meetings.route && !coursesLoaded) {
//                // Intercept when the course is not fully loaded and give a prompt
//                ErrorNotifier.showError("The course list has not been fully loaded yet. Please try again later.")
//                return@StudentCourseTopNav
//            }
//            if (destination != currentRoute) {
//                navController.navigate(destination) {
//                    popUpTo(navController.graph.startDestinationId) { saveState = false }
//                    launchSingleTop = true
////                    restoreState = true
//                }
//            }
            navController.navigate(destination) {
                popUpTo(navController.graph.startDestinationId) { saveState = false }
                launchSingleTop = true
                restoreState = true
            }
        }

        when (currentRoute) {
            StudentNavRoutes.Courses.route -> {
                // 3. Call onCoursesLoaded() after CoursesScreen is loaded.
                CoursesScreen(
                    navController = navController,
                    onCoursesLoaded = { ids ->
                        // Here I got the tutorIds returned by CoursesScreen
                        tutorIds = ids
                        Log.d("CourseHost", "当前可选 tutorIds = $tutorIds")
                        coursesLoaded = true
                    }
                )
            }

            StudentNavRoutes.Meetings.route -> {
//                if (!coursesLoaded || tutorIds.isEmpty()) {
//                    // Before tutors was fully loaded, a placeholder was displayed
//                    Box(Modifier.fillMaxSize(), Alignment.Center) {
//                        Text("Loading tutors…")
//                    }
//                } else {
//                    // tutorIds must be non-empty before they are truly rendered
//                    key(tutorIds) {
//                        MeetingScreen(tutorIds = tutorIds)
//                    }
//                }
                MeetingScreen()
            }


            else -> {
                Text("Unknown route")
            }
        }
    }
}