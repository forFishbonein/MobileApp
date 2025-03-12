package com.example.tutoring.ui.screens.tutor.common

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tutoring.data.Lesson
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.google.gson.Gson
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCardTutor(
    lesson: Lesson,
    modifier: Modifier = Modifier,
    onChangeComplete: () -> Unit,
    courseId: Int?,
    navController: NavHostController
) {
//    var currentStatus by remember { mutableStateOf(lesson.completed) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部标题
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary, // 使用主题的主色
                    letterSpacing = 1.2.sp,
                    shadow = Shadow(
                        color = Color.Gray,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
//            val encodedHtml = URLEncoder
//                .encode(lesson.content, StandardCharsets.UTF_8.toString())
//                .replace("+", "%20")
//            // 使用 WebView 显示 HTML
//            val webViewState = rememberWebViewState(
//                // data:text/html 指定 MIME 类型为 text/html
//                url = "data:text/html;charset=utf-8,$encodedHtml"
//            )
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ) {
//                    WebView(
//                        state = webViewState,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }

            Column(modifier = Modifier
                .padding(16.dp)
                .heightIn(min = 375.dp)){
                Text(
                    text = lesson.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (lesson.imageUrls.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Relevant Images:", style = MaterialTheme.typography.bodyMedium)
                        // 遍历 imageUrls，使用 Coil AsyncImage 显示图片
                        lesson.imageUrls.split(",").forEach { url ->
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
                if (lesson.pdfUrls.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Relevant PDFs:", style = MaterialTheme.typography.bodyMedium)
                        // 遍历 pdfUrls，显示为可点击的链接
                        lesson.pdfUrls.split(",").forEachIndexed { index, url ->
                            // 获取下划线后面的部分作为文件名
                            val fileName = url.substringAfterLast("_")
                            TextButton(
                                onClick = {
                                    // 点击后打开浏览器查看 PDF，这里使用 Android 的 Intent 方式
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(url)
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                                // 显示形如: "1. myfile.pdf"
                                Text("${index + 1}. $fileName", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // 在卡片底部右下角添加按钮
            Row(
                modifier = Modifier.fillMaxWidth().padding(top=16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        //TODO 使用 ViewModel 共享作用域，而不是路由传参 lesson
                        val lessonJson = Gson().toJson(lesson)
                        val encodedLesson = URLEncoder.encode(lessonJson, "UTF-8")
                        navController.navigate("tutor_add_lesson?courseId=${-1}&lesson=$encodedLesson")
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text("Update Content")
                }
                Button(
                    onClick = {
                        showConfirmDialog = true
                    },
                    shape = RoundedCornerShape(50),
                    enabled = !lesson.completed
                ) {
                    Text("Mark as Completed")
                }
            }
            // 显示当前状态
            Text(
                text = "Status: ${if (lesson.completed) "completed" else "in progress"}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to update content?") },
            confirmButton = {
                Button(
                    onClick = {
                        // 用户确认后调用接口
                        onChangeComplete()  // 假设这是你调用接口的方法
                        showConfirmDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}

