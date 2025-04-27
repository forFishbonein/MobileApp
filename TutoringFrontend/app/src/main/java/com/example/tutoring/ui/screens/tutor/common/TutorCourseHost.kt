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
            // 如果当前路由不同再导航，避免重复入栈
            if (destination != currentRoute) {
                navController.navigate(destination) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
        // 根据 currentRoute 显示不同的内容
        when (currentRoute) {
            TutorNavRoutes.Courses.route -> {
                // 课程列表页面
                CoursesScreen(
                    navController
                )
            }
            TutorNavRoutes.Meetings.route -> {
                // 预约管理页面
                MeetingScreen()
            }
            else -> {
                // 兜底
                Text("Unknown route")
            }
        }
    }
}
