package com.example.mychatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mychatapp.ui.screens.onboarding.OnboardingContact
import com.example.mychatapp.ui.screens.onboarding.AuthenticationEnterCode
import com.example.mychatapp.ui.screens.onboarding.LoginUserProfileScreen
import com.example.mychatapp.ui.screens.onboarding.OnboardingScreen
import com.example.mychatapp.ui.screens.onboarding.PhoneNumberAuthentication
import com.example.mychatapp.ui.theme.MyChatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyChatAppTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    MyChatApp()
                }
            }
        }
    }
}

@Composable
fun MyChatApp() {
    val navController = rememberNavController()
    MyChatAppTheme {
        NavHost(
            navController = navController,
            startDestination = "onboarding"
        ) {
            composable("onboarding") {
                OnboardingScreen(navController)
            }
            composable ("PhoneNumber"){
                PhoneNumberAuthentication(navController)
            }
            composable (
                route = "NumberCode/{phoneNumber}",
                arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
            ){ backStackEntry ->
                val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

                AuthenticationEnterCode(navController, phoneNumber)
            }
            composable("UserProfile"){
                LoginUserProfileScreen(navController)
            }
            composable("contacts"){
                OnboardingContact()
            }
            composable("home") {
                OnboardingScreen(navController)
            }
        }
    }
}


