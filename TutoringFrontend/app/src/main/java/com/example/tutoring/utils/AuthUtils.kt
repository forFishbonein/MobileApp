package com.example.tutoring.utils

import android.content.Context
import android.util.Log
import com.example.tutoring.data.Role

/**
 * 从 SharedPreferences 中获取用户登录信息
 * @param context 应用或 Activity 上下文
 * @return 如果用户已登录，则返回对应的 Role（例如 Role.STUDENT 或 Role.TUTOR），否则返回 null
 */
fun getRoleFromLogin(context: Context): Role? {
    // 获取存储用户信息的 SharedPreferences（例如 "user_prefs"）
    val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().clear().apply() // TODO 手动清空
    // 获取保存在 SharedPreferences 中的用户角色，假设保存的键名为 "user_role"
    val roleString = sharedPrefs.getString("user_role", null)
    Log.d("roleString", "$roleString")
    // 根据存储的字符串转换为 Role 枚举
    return when (roleString) {
        "STUDENT" -> Role.STUDENT
        "TUTOR" -> Role.TUTOR
        else -> null  // 未登录或无角色信息
    }
}
