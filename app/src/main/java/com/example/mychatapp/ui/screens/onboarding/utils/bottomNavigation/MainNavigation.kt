package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    var selectedIndex by remember { mutableIntStateOf(0) }

    val screens = listOf("contacts", "chats", "more")

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    navController.navigate(screens[index]) {
                        // Tránh tạo nhiều instance của cùng 1 screen
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "contacts",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("contacts") { OnboardingContacts() }
            composable("chats") { OnboardingChats() }
            composable("more") { OnboardingMores() }
        }
    }
}
