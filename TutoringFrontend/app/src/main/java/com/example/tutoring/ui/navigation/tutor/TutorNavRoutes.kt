package com.example.tutoring.ui.navigation.tutor

import com.example.tutoring.ui.navigation.student.StudentNavRoutes

sealed class TutorNavRoutes(val route: String) {
    data object Home : TutorNavRoutes("tutor_home")
    data object Application : TutorNavRoutes("tutor_application")
    data object Courses : TutorNavRoutes("tutor_courses")
    data object Profile : TutorNavRoutes("tutor_profile")
    data object Lessons : TutorNavRoutes("tutor_lessons/{courseId}")
    data object AddLesson : TutorNavRoutes("tutor_add_lesson?courseId={courseId}&lesson={lesson}")
}
