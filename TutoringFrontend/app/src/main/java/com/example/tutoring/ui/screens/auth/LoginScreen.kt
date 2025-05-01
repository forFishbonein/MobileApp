package com.example.tutoring.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginSuccess: (Role) -> Unit,
    onNavigateToRegister: () -> Unit,
    context: Context
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val apiService = NetworkClient.createService(ApiService::class.java)

    // --- “忘记密码”两步弹窗相关 state ---
    var showResetDialog by remember { mutableStateOf(false) }      // 第一步：输入邮箱
    var showNewPasswordDialog by remember { mutableStateOf(false) }// 第二步：输入新密码
    var resetEmail by remember { mutableStateOf("") }
    var resetToken by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
        // Forgot Password 链接，靠右显示
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = {
                    /* 打开重置密码流程 */
                    resetEmail = email  // 如果登录框里已经填了 email，可以预填
                    showResetDialog = true
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    "Forgot Password?"
                )
            }
        }
        // --- 第一步弹窗：输入邮箱 ---
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = {
                    Text(
                    "Reset Password",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp  // 比默认 titleLarge 小一号
                    ))
                },
                text = {
                    Column {
                        Text("Enter your email to receive a reset code.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // 调用 forgotPassword
                            showResetDialog = false
                            scope.launch {
                                try {
                                    val resp = apiService.forgotPassword(mapOf("email" to resetEmail))
                                    ErrorNotifier.showSuccess("A reset code has been sent to your email.")
                                    // 弹出第二步，让用户输入新密码
                                    showNewPasswordDialog = true
                                } catch (e: Exception) {
                                    ErrorNotifier.showError(e.message ?: "Network error.")
                                }
                            }
                        },
                        enabled = resetEmail.isNotBlank(),
                    ) {
                        Text("Send")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        // --- 第二步弹窗：输入 Token 和新密码 ---
        if (showNewPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showNewPasswordDialog = false },
                title   = { Text("Reset Your Password",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp  // 比默认 titleLarge 小一号
                    )) },
                text    = {
                    Column {
                        // 1) 验证码输入
                        OutlinedTextField(
                            value = resetToken,
                            onValueChange = { resetToken = it },
                            label = { Text("Verification Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        // 2) 新密码
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        // 3) 确认密码
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // 校验三项都不空，且两次密码一致
                        when {
                            resetToken.isBlank() ->
                                ErrorNotifier.showError("Please enter the verification code.")
                            newPassword.isBlank() || newPassword != confirmPassword ->
                                ErrorNotifier.showError("Passwords must match and not be empty.")
                            else -> {
                                showNewPasswordDialog = false
                                scope.launch {
                                    try {
                                        val resp = apiService.resetPassword(
                                            mapOf(
                                                "token"       to resetToken,
                                                "newPassword" to newPassword
                                            )
                                        )
                                        ErrorNotifier.showSuccess("Password has been reset. Please log in.")
                                    } catch (e: Exception) {
                                        ErrorNotifier.showError(e.message ?: "Network error.")
                                    }
                                }
                            }
                        }
                    }) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
//        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    ErrorNotifier.showError("Please fill all fields")
                } else {
                    scope.launch {
                        try {
                            val requestBody = mapOf(
                                "email" to email,
                                "password" to password,
                            )
                            val response = apiService.login(requestBody)

                            @Suppress("UNCHECKED_CAST")
                            val role = Role.valueOf((response.data as Map<String, Any>)["role"].toString().uppercase())

                            @Suppress("UNCHECKED_CAST")
                            val token = (response.data as Map<String, Any>)["token"].toString()

                            val response2 = apiService.getMyProfile("Bearer $token")

                            val info = response2.data
                            if (info != null) {
                                saveUserData(context, role.name, token, info)
                                onLoginSuccess(role)
                                ErrorNotifier.showSuccess( "Login successful!")
                            }else{
                                ErrorNotifier.showError( "Login failed.")
                            }
                        } catch (e: Exception) {
//                            ErrorNotifier.showError(e.message ?: "Login failed.")
                        }
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
    val userInfoJson = gson.toJson(userInfo)
    with(sharedPrefs.edit()) {
        putString("user_role", role) // "STUDENT" or "TUTOR"
        putString("token", token)
        putString("user_info", userInfoJson)
        apply()
    }
}
