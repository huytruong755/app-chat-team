package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.addFriendScreen.OnboardingAddFriend

@Composable
fun MainScreen(rootNavController: NavController) {
    // ⚠️ Tạo NavController riêng cho bottom bar
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        // Ẩn bottom bar nếu đang ở addFriend
        bottomBar = {
            if (currentRoute != "addFriend") {
                BottomNavigationBar(bottomNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "contacts",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("contacts") {
                OnboardingContacts(bottomNavController)
            }
            composable("messages") {
                OnboardingChats()
            }
            composable("profile") {
                OnboardingProfile()
            }
            composable("addFriend") {
                OnboardingAddFriend(bottomNavController)
            }
        }
    }
}
