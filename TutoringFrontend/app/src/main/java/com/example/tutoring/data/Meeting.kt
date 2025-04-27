package com.example.tutoring.data

data class Meeting(
    val bookingId: Int,
    val studentId: Int,
    val studentNickname: String,
    val tutorNickname: String,
    val content: String,
    val startTime: String,     // e.g. "2025-04-27 02:40"
    val endTime: String,       // e.g. "2025-04-27 03:40"
    val status: String,        // e.g. "Pending"
    val createdAt: String      // e.g. "2025-04-27 08:22"
)
