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
    // 1) 保存 tutorIds
    var tutorIds by remember { mutableStateOf<List<Int>>(emptyList()) }
    // 2) 标记课程加载完成
    var coursesLoaded by remember { mutableStateOf(false) }

    Column {
        StudentCourseTopNav(currentRoute) { destination ->
//            if (destination == StudentNavRoutes.Meetings.route && !coursesLoaded) {
//                // 课程没加载完时拦截，并给提示
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
                // 3. CoursesScreen 加载完成后调用 onCoursesLoaded()
                CoursesScreen(
                    navController = navController,
                    onCoursesLoaded = { ids ->
                        // 这里拿到 CoursesScreen 回传的 tutorIds
                        tutorIds = ids
                        Log.d("CourseHost", "当前可选 tutorIds = $tutorIds")
                        coursesLoaded = true
                    }
                )
            }

            StudentNavRoutes.Meetings.route -> {
//                if (!coursesLoaded || tutorIds.isEmpty()) {
//                    // 还没加载完 tutors，就显示占位
//                    Box(Modifier.fillMaxSize(), Alignment.Center) {
//                        Text("Loading tutors…")
//                    }
//                } else {
//                    // tutorIds 一定是非空，才真正去渲染
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