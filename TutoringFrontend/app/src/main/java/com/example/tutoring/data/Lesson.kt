package com.example.tutoring.data

data class Lesson(
    val completed: Boolean = false,
    val content: String = "",
    val courseId: Int = 0,
    val createdAt: String? = "",
    val imageUrls: String = "",
    val lessonId: Int? = 0,
    val pdfUrls: String = "",
    val title: String = "",
    val updatedAt: String? = ""
)
