package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.mores

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
//import com.example.mychatapp.model.ProfileViewModel

@Composable
fun OnboardingProfile(
    navController: NavController,
//    viewModel: ProfileViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "More Screen",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp)
        )
    }
}