package com.example.tutoring.data

data class Course(
    val courseId: Int = 0,
    val courseName: String = "",
    val createdAt: String? = null,  // 可为空
    val description: String = "",
    val subject: String = "",
    val tutorId: Int = 0,
    val updatedAt: String? = null,  // 可为空
    val status: String = "",
    val teacherName: String = ""
)

