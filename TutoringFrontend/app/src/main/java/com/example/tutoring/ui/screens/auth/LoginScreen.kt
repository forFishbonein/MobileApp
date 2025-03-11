package com.example.tutoring.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.tutoring.data.Role

@Composable
fun LoginScreen(
    onLoginSuccess: (Role) -> Unit,
    onNavigateToRegister: () -> Unit,
    context: Context
) {
    Column {
        Text(text = "登录页面")
        // 在此添加用户名、密码输入框
        Button(onClick = {
            // 登录逻辑，验证成功后调用 onLoginSuccess(role)
            // 示例中，假设登录成功后角色为 STUDENT
            onLoginSuccess(Role.STUDENT)
            saveUserRole(context, Role.STUDENT)
        }) {
            Text("登录")
        }
        TextButton(onClick = { onNavigateToRegister() }) {
            Text("还没有账号？注册")
        }
    }
}
fun saveUserRole(context: Context, role: Role) {
    val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with(sharedPrefs.edit()) {
        putString("user_role", role.name) // role.name 得到 "STUDENT" 或 "TUTOR"
        apply()
    }
}
