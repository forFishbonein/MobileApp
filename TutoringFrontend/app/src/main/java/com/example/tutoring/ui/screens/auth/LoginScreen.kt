package com.example.tutoring.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.tutoring.data.Role
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import com.google.gson.Gson
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: (Role) -> Unit,
    onNavigateToRegister: () -> Unit,
    context: Context
) {
    val scope = rememberCoroutineScope()
    // 账号、密码的本地状态
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val apiService = NetworkClient.createService(ApiService::class.java)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome back!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        // 构造请求体
                        val requestBody = mapOf(
                            "email" to email,
                            "password" to password,
                        )
                        // 调用接口
                        val response = apiService.login(requestBody)
                        val role = Role.valueOf(response.data?.role?.uppercase())
                        val token = response.data.token
                        // 调用接口
                        val response2 = apiService.getMyProfile(token)
                        onLoginSuccess(role)
                        // 存储用户数据等后续操作
//                        val info = mutableMapOf<String, Any>().apply {
//                            put("name", email.ifBlank { "张三" })
//                            put("age", 20)
//                        }
                        val info = response2.data
                        saveUserData(context, Role.TUTOR.name, "111111111", info)
                    } catch (e: Exception) {
                        ErrorNotifier.showError(e.message ?: "Login failed.")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { onNavigateToRegister() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Don't have an account? Register")
        }
    }
}
fun saveUserData(context: Context, role: String, token: String, userInfo: Any) {
    val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val userInfoJson = gson.toJson(userInfo)  // 将对象转换为 JSON 字符串
    with(sharedPrefs.edit()) {
        putString("user_role", role) // role.name 得到 "STUDENT" 或 "TUTOR"
        putString("token", token)
        putString("user_info", userInfoJson)
        apply()
    }
}
