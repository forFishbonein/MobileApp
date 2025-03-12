package com.example.tutoring.ui.screens.tutor

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tutoring.data.UserInfo
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLoginOut: () -> Unit
) {
    // 模拟数据（实际应从 ViewModel 或网络请求中获取）
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf(".") }
    var avatarUrl by remember { mutableStateOf("") }

    val context = LocalContext.current
    // 控制编辑状态
    var isEditing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    // 从 SharedPreferences 中获取 user_info JSON 并解析
    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userInfoJson = sharedPrefs.getString("user_info", null)
        if (!userInfoJson.isNullOrBlank()) {
            val gson = Gson()
            val userInfo = gson.fromJson(userInfoJson, UserInfo::class.java)
            email = userInfo.email
            role = userInfo.role
            createdAt = userInfo.createdAt
            nickname = userInfo.nickname
            bio = userInfo.bio
            avatarUrl = userInfo.avatarUrl
        }
    }
    // 创建图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // 获取 MIME 类型
                val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
                // 获取文件扩展名
                val extension = android.webkit.MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(mimeType) ?: "jpg"
                // 读取文件内容
                val inputStream = context.contentResolver.openInputStream(it)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    // 构建 RequestBody 和 MultipartBody.Part
                    val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val multipartPart = MultipartBody.Part.createFormData(
                        "file",  // 后端接口要求的字段名，这里假设为 "file"
                        "image_${System.currentTimeMillis()}.$extension",
                        requestBody
                    )
                    coroutineScope.launch {
                        try {
                            val response = apiService.uploadAvatar(multipartPart)
                            // 上传成功后，从 response.data 获取新的图片 URL（根据后端返回的数据格式）
                            val newAvatarUrl = response.data.toString()
                            avatarUrl = newAvatarUrl
                            // 更新 SharedPreferences 中的 user_info
                            val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            val gson = Gson()
                            val userInfoJson = sharedPrefs.getString("user_info", null)
                            if (!userInfoJson.isNullOrBlank()) {
                                // 解析当前存储的 userInfo
                                val userInfo = gson.fromJson(userInfoJson, UserInfo::class.java)
                                // 更新 bio 和 nickname
                                val updatedUserInfo = userInfo.copy(
                                    avatarUrl = newAvatarUrl
                                )
                                // 写回 SharedPreferences
                                with(sharedPrefs.edit()) {
                                    putString("user_info", gson.toJson(updatedUserInfo))
                                    apply()
                                }
                            }
                            ErrorNotifier.showSuccess("Update Successful!")
                        } catch (e: Exception) {
                            ErrorNotifier.showError("Update exception: ${e.message}")
                        }
                    }
                } else {
                    ErrorNotifier.showError("File reading failed")
                }
            }
        }
    )
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 头像：位于最上方居中，点击可更新头像（此处模拟直接修改 URL）
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable {
                    imagePickerLauncher.launch("image/*")
                }
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop  // 关键：让图片裁剪填充整个圆形
            )
        }
        Text(
            text = "Tap avatar to change",
            style = MaterialTheme.typography.labelSmall
        )

        // 静态信息卡片：显示邮箱、角色、账号创建时间（不可编辑）
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { },
                    label = { Text("Email") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    )
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { },
                    label = { Text("Role") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    )
                )
                OutlinedTextField(
                    value = createdAt,
                    onValueChange = { },
                    label = { Text("Account Created At") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    )
                )
            }
        }

        // 可编辑信息卡片：昵称和个人简介（bio）可以修改，点击“Save Changes”调用接口
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nickname") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    )
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    )
                )
                // 按钮：根据编辑状态显示 "Update" 或 "Save Changes"
                Button(
                    onClick = {
                        if (isEditing) {
                            // 保存更新：调用接口
                            coroutineScope.launch {
                                try {
                                    // 构造请求体
                                    val requestBody = mapOf(
                                        "bio" to bio,
                                        "nickname" to nickname,
                                    )
                                    // 调用接口
                                    val response = apiService.updateMyProfile(requestBody)
                                    ErrorNotifier.showSuccess( "Update successful!")
                                    // 更新 SharedPreferences 中的 user_info
                                    val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    val gson = Gson()
                                    val userInfoJson = sharedPrefs.getString("user_info", null)
                                    if (!userInfoJson.isNullOrBlank()) {
                                        // 解析当前存储的 userInfo
                                        val userInfo = gson.fromJson(userInfoJson, UserInfo::class.java)
                                        // 更新 bio 和 nickname
                                        val updatedUserInfo = userInfo.copy(
                                            bio = bio,
                                            nickname = nickname
                                        )
                                        // 写回 SharedPreferences
                                        with(sharedPrefs.edit()) {
                                            putString("user_info", gson.toJson(updatedUserInfo))
                                            apply()
                                        }
                                    }
                                } catch (e: Exception) {
                                    ErrorNotifier.showError(e.message ?: "Update failed.")
                                }
                            }
                            isEditing = false
                        } else {
                            // 切换到编辑状态
                            isEditing = true
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (isEditing) "Save Changes" else "Update")
                }

            }
        }

        // Logout 按钮
        Button(
            onClick = { onLoginOut() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout", color = MaterialTheme.colorScheme.onError)
        }
    }
}
