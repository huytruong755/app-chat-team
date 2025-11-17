package com.example.mychatapp.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mychatapp.R
import com.example.mychatapp.model.modelData.Contact
import com.example.mychatapp.model.viewModel.ContactRepository
import com.example.mychatapp.ui.screens.onboarding.utils.SessionManager
import kotlinx.coroutines.launch
import com.example.mychatapp.network.dto.RegisterRequestDto
import com.example.mychatapp.network.RetrofitInstance
import android.widget.Toast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUserProfileScreen (
    navController: NavController,
    phoneNumber: String
) {
    BackHandler {
        // Để trống nghĩa là chặn không cho quay lại
    }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    //báo lỗi
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val contactRepository = ContactRepository

    val apiService = RetrofitInstance.api
    var isLoading by remember { mutableStateOf(false) } // (Để chặn spam click)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Profile",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 23.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Avatar + nút thêm
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.modellogin),
                        contentDescription = "Profile Icon",
                        tint = Color(0xFF1E1E1E),
                        modifier = Modifier.size(60.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF000000))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { /* TODO: chọn ảnh */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.moremodellogin),
                        contentDescription = "Add Photo",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // First name
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    if (it.isNotBlank()) firstNameError = false
                },
                placeholder = { Text("First Name (Required)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF9F9F9),
                    unfocusedContainerColor = Color(0xFFF9F9F9),
                    focusedBorderColor = if (firstNameError) Color.Red else Color.Transparent,
                    unfocusedBorderColor = if (firstNameError) Color.Red else Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            if (firstNameError) {
                Text(
                    text = "First name cannot be empty",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Last name
            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    if (it.isNotBlank()) lastNameError = false
                },
                placeholder = { Text("Last Name (Required)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF9F9F9),
                    unfocusedContainerColor = Color(0xFFF9F9F9),
                    focusedBorderColor = if (lastNameError) Color.Red else Color.Transparent,
                    unfocusedBorderColor = if (lastNameError) Color.Red else Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            if (lastNameError) {
                Text(
                    text = "Last name cannot be empty",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Save button
            Button(
                onClick = {
                    val isFirstNameEmpty = firstName.isBlank()
                    val isLastNameEmpty = lastName.isBlank()

                    firstNameError = isFirstNameEmpty
                    lastNameError = isLastNameEmpty

                    if (!isFirstNameEmpty && !isLastNameEmpty && !isLoading) {
                        isLoading = true // Chặn spam click

                        // "Công thức" Register (khớp Postman)
                        val registerDto = RegisterRequestDto(
                            firstName = firstName,
                            lastName = lastName,
                            phoneNumber = phoneNumber
                            // lastSeen: Dùng giá trị mặc định (từ ApiService.kt)
                        )

                        coroutineScope.launch {
                            try {
                                // 1. BÁO CHO C# (Register)
                                val response = apiService.register(registerDto)

                                if (response.isSuccessful) {
                                    // 2. LẤY DỮ LIỆU USER (Token, ID)
                                    val cSharpResponse = response.body()
                                    if (cSharpResponse != null) {

                                        // 3. LƯU PHIÊN ĐĂNG NHẬP (Cục bộ)
                                        sessionManager.saveUserSession(
                                            id = cSharpResponse.userId,
                                            name = "$firstName $lastName"
                                        )

                                        // (Tùy chọn: Lưu vào Contact Repository)
                                        contactRepository.addContact(
                                            Contact(
                                                id = cSharpResponse.userId,
                                                name = "$firstName $lastName",
                                                status = "Online",
                                                isOnline = true,
                                                avatarUrl = null
                                            )
                                        )

                                        // 4. ĐI ĐẾN MÀN HÌNH CHÍNH
                                        isLoading = false
                                        navController.navigate("mainScreen") {
                                            popUpTo(navController.graph.startDestinationRoute!!) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, "API Error: Empty response body", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Lỗi 400 (ví dụ: "Email already taken", dù không thể)
                                    isLoading = false
                                    Toast.makeText(context, "API Error: ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                // Lỗi "Failed to connect"
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0056FF),
                    disabledContainerColor = Color(0xFFB0C4FF)
                )
            ) {
                Text(
                    text = "Save",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}