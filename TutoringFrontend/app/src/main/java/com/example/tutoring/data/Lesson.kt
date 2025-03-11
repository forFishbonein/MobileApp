package com.example.tutoring.data

data class Lesson(
    val id: Int,
    val title: String,
    val status: String,     // "completed", "locked", "in-progress" 等
    val contentHtml: String
)
