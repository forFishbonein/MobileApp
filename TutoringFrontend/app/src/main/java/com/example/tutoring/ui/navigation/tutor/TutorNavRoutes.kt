package com.example.tutoring.ui.navigation.tutor

import com.example.tutoring.ui.navigation.student.StudentNavRoutes

sealed class TutorNavRoutes(val route: String) {
    object Home : TutorNavRoutes("tutor_home")
    object Application : TutorNavRoutes("tutor_application")
    object Courses : TutorNavRoutes("tutor_courses")
    object Profile : TutorNavRoutes("tutor_profile")
    object Lessons : TutorNavRoutes("tutor_lessons/{courseId}")
    object AddLesson : TutorNavRoutes("tutor_add_lesson?courseId={courseId}&lesson={lesson}")
}
