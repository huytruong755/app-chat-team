package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mychatapp.ui.screens.onboarding.AuthenticationEnterCode
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.addFriendScreen.OnboardingAddFriend
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.addFriendScreen.OnboardingContacts
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.chats.OnboardingChats
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.mores.OnboardingProfile

@Composable
fun MainScreen(navController: NavController) {
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
                OnboardingChats(bottomNavController)
            }
            composable("profile") {
                OnboardingProfile(bottomNavController)
            }
            composable("addFriend") {
                OnboardingAddFriend(bottomNavController)
            }
            composable (
                route = "NumberCode/{friendPhoneNumber}",
                arguments = listOf(navArgument("friendPhoneNumber") { type = NavType.StringType })
            ){ backStackEntry ->
                // lấy argument "friendPhoneNumber" và gán vào biến tên "phoneNumber"
                val phoneNumber = backStackEntry.arguments?.getString("friendPhoneNumber") ?: ""

                // truyền biến "phoneNumber" (ở trên) vào đây,
                // không phải truyền "friendPhoneNumber" (là tên của key)
                AuthenticationEnterCode(bottomNavController, phoneNumber)
            }
        }
    }
}