package com.example.tutoring.data

data class Course(
    val courseId: Int = 0,
    val courseName: String = "",
    val createdAt: String? = null,
    val description: String = "",
    val subject: String = "",
    val tutorId: Int = 0,
    val updatedAt: String? = null, // NullableTypes
    val status: String = "",
    val teacherName: String = ""
)

