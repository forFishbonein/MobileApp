package com.example.tutoring.data

data class AvailabilitySlotDetail(
    val availabilityId: Int,
    val tutorId: Int,
    val startTime: String,   // e.g. "2025-04-27 02:40"
    val endTime: String,     // e.g. "2025-04-27 03:40"
    val isBooked: Boolean,
    val createdAt: String,   // e.g. "2025-04-27 00:40"
    val updatedAt: String    // e.g. "2025-04-27 00:40"
)
