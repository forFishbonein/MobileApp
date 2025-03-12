package com.example.tutoring.ui.screens.student.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tutoring.data.Lesson
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCard(
    lesson: Lesson,
    modifier: Modifier = Modifier
) {

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

            // 富文本内容
            val encodedHtml = URLEncoder
                .encode(lesson.content, StandardCharsets.UTF_8.toString())
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
                    .heightIn(min = 550.dp, max = 550.dp)
            ) {
                WebView(
                    state = webViewState,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

