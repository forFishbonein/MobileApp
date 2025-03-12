package com.example.tutoring.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLoginOut: () -> Unit
) {
    // 模拟初始数据，实际项目中从 ViewModel 或网络请求中获取
    var email by remember { mutableStateOf("user@example.com") }
    var role by remember { mutableStateOf("Student") }
    var createdAt by remember { mutableStateOf("2025-01-01") }
    var nickname by remember { mutableStateOf("UserNickname") }
    var bio by remember { mutableStateOf("This is my bio. Feel free to update it.") }
    var avatarUrl by remember { mutableStateOf("https://example.com/avatar.jpg") }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 不可编辑信息
            OutlinedTextField(
                value = email,
                onValueChange = { },
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = role,
                onValueChange = { },
                label = { Text("Role") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = createdAt,
                onValueChange = { },
                label = { Text("Account Created At") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            // 可编辑信息：昵称
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
                modifier = Modifier.fillMaxWidth()
            )

            // 可编辑信息：个人简介
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
            )

            // 可编辑信息：头像
            // 点击头像后可弹出图片选择器或直接更新
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable {
                        // 此处可以调用图片选择器更新头像
                        // 这里模拟直接修改头像 URL
                        avatarUrl = "https://example.com/new-avatar.jpg"
                    }
            )
            Text(
                text = "Tap avatar to change",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 退出登录按钮
            Button(
                onClick = { onLoginOut() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
