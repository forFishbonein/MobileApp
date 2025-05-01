package com.example.tutoring.data


//data class StudentProgress(
//    val studentName: String,
//    val lessonsCompleted: Int,
//    val totalLessons: Int
//)
//
//data class CourseStats(
//    val courseName: String,
//    val enrolledStudents: Int,
//    val studentProgressList: List<StudentProgress>
//)
data class CourseProgress(
    val courseId: Int,
    val courseName: String,
    val studentCount: Int,
    val students: List<StudentProgress>
)

data class StudentProgress(
    val studentId: Int,
    val nickname: String,
    val progressPercent: Int
)
//val mockCourses = listOf(
//    CourseStats(
//        "Intro to AI", 3, listOf(
//            StudentProgress("Alice", 2, 4),
//            StudentProgress("Bob", 4, 4),
//            StudentProgress("Charlie", 1, 4)
//        )
//    ),
//    CourseStats(
//        "Data Science", 2, listOf(
//            StudentProgress("Dana", 1, 5),
//            StudentProgress("Evan", 5, 5)
//        )
//    )
//)
