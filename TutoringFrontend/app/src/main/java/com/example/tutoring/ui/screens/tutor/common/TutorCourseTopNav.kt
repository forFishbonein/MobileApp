package com.example.tutoring.ui.screens.tutor.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * 顶部导航栏：两个 Tab，分别跳转到 tutor_courses 和 tutor_meeting
 *
 * @param currentRoute 当前选中的路由名（"tutor_courses" 或 "tutor_meeting"）
 * @param onNavigate 点击时回调，传出目标路由
 */
@Composable
fun TutorCourseTopNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    // 定义两个标签及对应路由
    val tabs = remember {
        listOf(
            "Courses" to "tutor_courses",
            "Meetings" to "tutor_meeting"
        )
    }
    // 当前选中索引
    val selectedIndex = tabs.indexOfFirst { it.second == currentRoute }.coerceAtLeast(0)

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        tabs.forEachIndexed { index, pair ->
            val (label, route) = pair
            Tab(
                selected = index == selectedIndex,
                onClick = { onNavigate(route) },
                text = { Text(label) },
//                icon = {
//                    Icon(
//                        imageVector = if (route == "tutor_courses") Icons.Filled.List else Icons.Filled.DateRange,
//                        contentDescription = label
//                    )
//                }
            )
        }
    }
}
