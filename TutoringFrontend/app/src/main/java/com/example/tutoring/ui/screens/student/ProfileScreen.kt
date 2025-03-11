package com.example.tutoring.ui.screens.student

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.tutoring.utils.logoutClear

@Composable
fun ProfileScreen(onLoginOut: () -> Unit) {


    Column {
        Text(text = "这是个人中心")
        Button(onClick = { onLoginOut() }) {
            Text(text = "退出登录")
        }
    }
}