package com.example.tutoring.ui.screens.student.common

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
 * Top navigation bar: Two tabs, which jump to tutor_courses and tutor_meeting respectively
 *
 * @param currentRoute The currently selected route name ("tutor_courses" or "tutor_meeting")
 * @param onNavigate When clicked, it is called back and the target route is sent out
 */
@Composable
fun StudentCourseTopNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    // Define two labels and the corresponding routes
    val tabs = remember {
        listOf(
            "Courses" to "student_courses",
            "Meetings" to "student_meeting"
        )
    }
    // Currently selected index
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
