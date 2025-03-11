package com.example.tutoring.ui.navigation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import com.example.tutoring.ui.navigation.student.StudentNavRoutes
import com.example.tutoring.ui.navigation.tutor.TutorNavRoutes

object NavBarItems {

    val StudentBarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.Home,         // 可换其他Material图标
            route = StudentNavRoutes.Home.route
        ),
        BarItem(
            title = "Courses",
            image = Icons.AutoMirrored.Filled.LibraryBooks,         // 替换为更合适的图标
            route = StudentNavRoutes.Courses.route
        ),
        BarItem(
            title = "Profile",
            image = Icons.Filled.Person,       // 个人页图标
            route = StudentNavRoutes.Profile.route
        )
    )

    val TutorBarItems = listOf(
        BarItem(
            title = "主页",
            image = Icons.Filled.Home,
            route = TutorNavRoutes.Home.route
        ),
        BarItem(
            title = "申请",
            image = Icons.AutoMirrored.Filled.Send, // 替换为更合适的图标
            route = TutorNavRoutes.Application.route
        ),
        BarItem(
            title = "课程",
            image = Icons.Filled.Book,
            route = TutorNavRoutes.Courses.route
        ),
        BarItem(
            title = "我的",
            image = Icons.Filled.Person,
            route = TutorNavRoutes.Profile.route
        ),
    )
}
