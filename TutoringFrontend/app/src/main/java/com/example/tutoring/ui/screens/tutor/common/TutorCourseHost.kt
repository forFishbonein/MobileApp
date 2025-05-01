package com.example.tutoring.ui.screens.tutor.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.tutoring.ui.navigation.tutor.TutorNavRoutes
import com.example.tutoring.ui.screens.tutor.CoursesScreen
import com.example.tutoring.ui.screens.tutor.MeetingScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TutorCourseHost(navController: NavHostController, currentRoute: String) {
    Column {
        TutorCourseTopNav(currentRoute) { destination ->
            // If the current route is different, navigate again to avoid duplicate pushing onto the stack
            if (destination != currentRoute) {
                navController.navigate(destination) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
        // Display different contents according to currentRoute
        when (currentRoute) {
            TutorNavRoutes.Courses.route -> {
                // Course list page
                CoursesScreen(
                    navController
                )
            }
            TutorNavRoutes.Meetings.route -> {
                // Reservation Management page
                MeetingScreen()
            }
            else -> {
                Text("Unknown route")
            }
        }
    }
}
