package com.example.tutoring.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.tutoring.data.Role

@Composable
fun RegisterScreen(
    onRegisterSuccess: (Role) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column {
        Text(text = "注册页面")
        // 在此添加注册表单：用户名、密码、确认密码等
        Button(onClick = {
            // 注册逻辑，注册成功后调用 onRegisterSuccess(role)
            // 示例中，假设注册成功后角色为 TUTOR
            onRegisterSuccess(Role.TUTOR)
        }) {
            Text("注册")
        }
        TextButton(onClick = { onNavigateToLogin() }) {
            Text("已有账号？去登录")
        }
    }
}
