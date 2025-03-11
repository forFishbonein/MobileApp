package com.example.tutoring.ui.screens.tutor
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    navController: NavHostController
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    // 标题
    var lessonTitle by remember { mutableStateOf("") }
    // 多行文本框内容
    var contentText by remember { mutableStateOf("") }
    // 记录上传后图片和 PDF 的链接（这里用 URI.toString() 代替真实链接）
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var pdfUrls by remember { mutableStateOf(listOf<String>()) }

    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)

    // 图片选择器：允许用户选择图片
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
                val extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

                // 使用 ContentResolver 读取文件内容
                val inputStream = context.contentResolver.openInputStream(it)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    // 构建 RequestBody 和 MultipartBody.Part，根据动态 MIME 类型生成
                    val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val multipartPart = MultipartBody.Part.createFormData(
                        "file",
                        "image_${System.currentTimeMillis()}.$extension",
                        requestBody
                    )
                    coroutineScope.launch {
                        try {
                            val response = apiService.uploadImage(multipartPart)
                            // 上传成功，将返回的图片 URL 添加到列表中
                            imageUrls = imageUrls + response.data.toString()
                        } catch (e: Exception) {
                            ErrorNotifier.showError("Upload exception: ${e.message}")
                        }
                    }
                } else {
                    ErrorNotifier.showError("File reading failed")
                }
            }
        }
    )

    // PDF 文件选择器
// PDF 文件选择器
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // 获取 MIME 类型，默认 "application/pdf"
                val mimeType = context.contentResolver.getType(it) ?: "application/pdf"
                // 根据 MIME 类型获取扩展名，默认 "pdf"
                val extension = android.webkit.MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(mimeType) ?: "pdf"

                // 使用 ContentResolver 读取文件内容
                val inputStream = context.contentResolver.openInputStream(it)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    // 构建 RequestBody 和 MultipartBody.Part
                    val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val multipartPart = MultipartBody.Part.createFormData(
                        "file",
                        "pdf_${System.currentTimeMillis()}.$extension",
                        requestBody
                    )
                    coroutineScope.launch {
                        try {
                            val response = apiService.uploadPdf(multipartPart)
                            // 上传成功，将返回的 PDF URL 添加到列表中
                            pdfUrls = pdfUrls + response.data.toString()
                        } catch (e: Exception) {
                            ErrorNotifier.showError("Upload exception: ${e.message}")
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 允许滚动
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题输入框
        OutlinedTextField(
            value = lessonTitle,
            onValueChange = { lessonTitle = it },
            label = { Text("Lesson Title") },
            modifier = Modifier
                .fillMaxWidth()
        )
        // 多行文本框（内容编辑区域）
        OutlinedTextField(
            value = contentText,
            onValueChange = { contentText = it },
            label = { Text("Lesson Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp), // 圆角更大，风格更柔和
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // 阴影稍微重一点，但不过分
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // 控制内部间距
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                ) {
                    Text("Upload Image")
                }
                // 显示上传后的图片区域
                if (imageUrls.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Uploaded Images:", style = MaterialTheme.typography.titleMedium)
                            // 遍历 imageUrls，使用 Coil AsyncImage 显示图片
                            imageUrls.forEach { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Uploaded Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
        Card(
            shape = RoundedCornerShape(16.dp), // 圆角更大，风格更柔和
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // 阴影稍微重一点，但不过分
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // 控制内部间距
            ) {
                Button(
                    onClick = { pdfPickerLauncher.launch("application/pdf") },
//                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload PDF")
                }
                // 显示上传后的 PDF 区域
                if (pdfUrls.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Uploaded PDFs:", style = MaterialTheme.typography.titleMedium)
                            // 遍历 pdfUrls，显示为可点击的链接
                            pdfUrls.forEach { url ->
                                TextButton(
                                    onClick = {
                                        // 点击后打开浏览器查看 PDF，这里使用 Android 的 Intent 方式
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(url)
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text(url, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 提交按钮
        Button(
            onClick = {
                // 此处调用后端 API，提交 contentText, imageUrls, pdfUrls
//                    onLessonAdded()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit Lesson")
        }
    }
}
