package com.example.tutoring.ui.screens.tutor.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tutoring.data.Lesson
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCardTutor(
    lesson: Lesson,
    modifier: Modifier = Modifier
) {
    var currentStatus by remember { mutableStateOf(lesson.status) }
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
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            val encodedHtml = URLEncoder
                .encode(lesson.contentHtml, StandardCharsets.UTF_8.toString())
                .replace("+", "%20")
            // 使用 WebView 显示 HTML
            val webViewState = rememberWebViewState(
                // data:text/html 指定 MIME 类型为 text/html
                url = "data:text/html;charset=utf-8,$encodedHtml"
            )

            // 外层再套一层 Box 或 Surface 控制高度
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 375.dp)
            ) {
                WebView(
                    state = webViewState,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 在卡片底部右下角添加按钮
            Row(
                modifier = Modifier.fillMaxWidth().padding(top=16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        // 将当前课程状态更新为 "Completed"
//                            currentStatus = "Completed"
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text("Update Content")
                }
                Button(
                    onClick = {
                        // 将当前课程状态更新为 "Completed"
                        currentStatus = "Completed"
                    },
                    shape = RoundedCornerShape(50),
                    enabled = currentStatus!="Completed"
                ) {
                    Text("Mark as Completed")
                }
            }
            // 显示当前状态
            Text(
                text = "Status: $currentStatus",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
}

