package com.example.tutoring.ui.navigation.auth

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tutoring.data.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthMainScreen(onLoginSuccess: (Role) -> Unit,context: Context) {
    // 内层自己维护的 NavController，仅负责 登录/注册 之间的切换
    val authNavController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("登录/注册") }
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
             AuthNavHost(authNavController, onLoginSuccess, context)
        }
    }
}
