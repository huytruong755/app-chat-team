package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mychatapp.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Contacts",
            selectedIcon = Icons.Filled.Person,
            unselectedIconRes = R.drawable.contact,
            route = "contacts"
        ),
        BottomNavItem(
            title = "Messages",
            selectedIcon = Icons.AutoMirrored.Filled.Message,
            unselectedIconRes = R.drawable.chat,
            route = "messages"
        ),
        BottomNavItem(
            title = "Profile",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIconRes = R.drawable.profile,
            route = "profile"
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    if (selected) {
                        if (item.selectedIcon != null) {
                            Icon(
                                imageVector = item.selectedIcon,
                                contentDescription = item.title
                            )
                        } else if (item.selectedIconRes != null) {
                            Icon(
                                painter = painterResource(id = item.selectedIconRes),
                                contentDescription = item.title
                            )
                        }
                    } else if (item.unselectedIconRes != null) {
                        Icon(
                            painter = painterResource(id = item.unselectedIconRes),
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.title,
                            color = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
