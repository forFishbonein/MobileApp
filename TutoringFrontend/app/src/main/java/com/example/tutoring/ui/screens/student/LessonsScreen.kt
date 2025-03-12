package com.example.tutoring.ui.screens.student
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoring.data.Lesson
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.screens.student.common.LessonCard
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class) // Accompanist Pager 注解
@Composable
fun LessonsScreen(courseId: Int?, loadingViewModel: LoadingViewModel = viewModel()) {
    // 全局加载指示器（overlay）
    if (loadingViewModel.isHttpLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    // TODO 根据courseId去查找对应的 course 内容
    var lessons by remember { mutableStateOf(listOf<Lesson>()) }
    // PagerState 控制当前显示第几页
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    fun getAllLessons(){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.listLessons(courseId)
                lessons = response.data as List<Lesson>
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
            }
        }
    }
    // 首次进入页面时加载数据
    LaunchedEffect(Unit) {
        getAllLessons()
    }
    // 导航按钮 Row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                // 点击返回上一课时
                if (pagerState.currentPage > 0) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            },
            enabled = pagerState.currentPage > 0
        ) {
            Text("Previous Lesson")
        }

        Button(
            onClick = {
                // 点击跳转到下一课时，仅当当前 lesson 的状态为 "completed" 时允许
                if (pagerState.currentPage < lessons.size - 1 &&
                    lessons[pagerState.currentPage].completed) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            enabled = pagerState.currentPage < lessons.size - 1 &&
                    lessons[pagerState.currentPage].completed
        ) {
            Text("Next Lesson")
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 创建一个横向 Pager
        HorizontalPager(
            state = pagerState,
            count = lessons.size,
            modifier = Modifier.fillMaxSize().padding(top=48.dp),
            userScrollEnabled = true // 禁止手势滑动，必须点击按钮
        ) { pageIndex ->
            val lesson = lessons[pageIndex]

            // Card 内容
            LessonCard(
                lesson = lesson
            )
        }
    }
}
