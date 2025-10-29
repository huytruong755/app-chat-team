package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.addFriendScreen.OnboardingAddFriend



@Composable
fun MainScreen(navController: NavController) {
    val navControllerbottomBar = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navControllerbottomBar) }
    ) { innerPadding ->
        NavHost(
            navController = navControllerbottomBar,
            startDestination = "contacts", // ✅ Contacts là màn hình đầu tiên
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("contacts") { OnboardingContacts(navControllerbottomBar) }
            composable("messages") { OnboardingChats() }
            composable("profile") { OnboardingProfile() }
            composable("addFriend") { OnboardingAddFriend(navControllerbottomBar)  }

        }
    }
}