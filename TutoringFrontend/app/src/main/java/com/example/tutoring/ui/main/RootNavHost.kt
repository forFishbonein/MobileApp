package com.example.tutoring.ui.main

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutoring.data.Role
import com.example.tutoring.ui.navigation.auth.AuthMainScreen
import com.example.tutoring.utils.getRoleFromLogin

@Composable
fun RootNavHost() {
    val context = LocalContext.current
    // 从启动时自动检查并恢复用户登录状态
    val initialRole = getRoleFromLogin(context)
    var isLoggedIn by remember { mutableStateOf(initialRole != null) }
    var userRole by remember { mutableStateOf(initialRole ?: Role.STUDENT) }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "auth"
    ) {
        // 认证流程
        composable("auth") {
            AuthMainScreen(
                onLoginSuccess = { role ->
                    // 登录成功后更新状态，并跳转到主界面
                    isLoggedIn = true
                    userRole = role
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                context
            )
        }
        // 主界面
        composable("main") {
            // 根据用户角色加载对应的主界面
            RootScreen(userRole = userRole)
        }
    }
}

