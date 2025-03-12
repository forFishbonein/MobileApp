package com.example.tutoring.ui.navigation.tutor

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.tutoring.ui.navigation.common.NavBarItems

@Composable
fun TutorBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // 可自行调配颜色
        contentColor = Color.White
    ) {
        NavBarItems.TutorBarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute?.startsWith(navItem.route) == true,
                onClick = {
                    // 点击时导航到对应路由
                    navController.navigate(navItem.route) {
                        // 跳转配置
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.image,
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}
