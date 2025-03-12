package com.example.tutoring.utils

import android.content.Context
import android.util.Log
import com.example.tutoring.data.Role


fun getRoleFromLogin(context: Context): Role? {
    // Gets SharedPreferences for storing user information
    val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    // Get user role
    val roleString = sharedPrefs.getString("user_role", null)
    Log.d("roleString", "$roleString")
    // Return the corresponding Role (such as role-.student or role-.tutor) if the user is logged in, or null otherwise
    return when (roleString) {
        "STUDENT" -> Role.STUDENT
        "TUTOR" -> Role.TUTOR
        else -> null
    }
}

fun logoutClear(context: Context) {
    val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().clear().apply()
}

