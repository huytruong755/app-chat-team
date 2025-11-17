package com.example.mychatapp.ui.screens.onboarding

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mychatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

import com.example.mychatapp.network.RetrofitInstance
import com.example.mychatapp.network.dto.FirebaseTokenDto
import com.example.mychatapp.ui.screens.onboarding.utils.SessionManager
import kotlinx.coroutines.launch
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationEnterCode(
    navController: NavController,
    phoneNumber: String,
    verificationId: String
) {
    var otpCode by remember { mutableStateOf("") }
    val maxLength = 6

    var isLoading by remember { mutableStateOf(false) } // Đổi tên
    var errorMessage by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val apiService = RetrofitInstance.api

    LaunchedEffect(otpCode) {
        // 💡 SỬA: Kiểm tra 6 số
        if (otpCode.length == maxLength) {
            isLoading = true
            errorMessage = ""

            // 1. Tạo "chìa khóa" (credential) từ ID phiên và OTP người dùng nhập
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)

            // 2. Dùng "chìa khóa" để đăng nhập
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 3. ĐĂNG NHẬP FIREBASE THÀNH CÔNG!
                        Toast.makeText(context, "Firebase Sign-In Success!", Toast.LENGTH_SHORT)
                            .show()

                        // 4. LẤY FIREBASE ID TOKEN (ĐỂ GỬI CHO C#)
                        task.result?.user?.getIdToken(true)
                            ?.addOnSuccessListener { tokenResult ->
                                val firebaseToken = tokenResult.token

                                if (firebaseToken == null) {
                                    isLoading = false
                                    errorMessage = "Failed to get valid token from Firebase."
                                    return@addOnSuccessListener
                                }
                                // TODO: (Bước sau) Gọi API C# tại đây
                                coroutineScope.launch {
                                    try {
                                        // 3. Gửi Token Firebase -> C#, nhận Token C#
                                        val cSharpResponse = apiService.firebaseLogin(
                                            FirebaseTokenDto(token = firebaseToken)
                                        )

                                        // 4. LƯU PHIÊN ĐĂNG NHẬP THẬT
                                        sessionManager.saveUserSession(
                                            id = cSharpResponse.userId, // Hoặc SĐT nếu C# không trả về
                                            name = cSharpResponse.name
                                        )

                                        Toast.makeText(context, "Login Success!", Toast.LENGTH_SHORT).show()

                                        // 5. Điều hướng đến UserProfile
                                        isLoading = false
                                        navController.navigate("UserProfile/$phoneNumber") {
                                            popUpTo("PhoneNumber") { inclusive = true }
                                        }

                                    } catch (e: Exception) {
                                        // 6. Lỗi khi gọi API C#
                                        isLoading = false
                                        errorMessage = "C# API Error: ${e.message}"
                                    }
                                }
                            }
                            ?.addOnFailureListener {
                                // 7. Lỗi khi lấy Firebase Token
                                isLoading = false
                                errorMessage = "Failed to get Firebase token: ${it.message}"
                            }

                    } else {
                        // 8. Lỗi (Sai OTP)
                        isLoading = false
                        errorMessage = "Invalid OTP code. Please try again."
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.vector),
                            contentDescription = "Back",
                            modifier = Modifier.size(15.dp),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->

        // Nội dung chính
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Enter Code",
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                lineHeight = 38.sp,
                modifier = Modifier.width(350.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "We have sent you an SMS with the code\n to $phoneNumber",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                lineHeight = 28.sp,
                modifier = Modifier.width(350.dp)
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Ẩn mã otp khi nhập
            TextField(
                value = otpCode,
                onValueChange = {
                    if (it.length <= maxLength && it.all { c -> c.isDigit() }) {
                        otpCode = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .width(1.dp)
                    .height(1.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(maxLength) { index ->
                    val char = otpCode.getOrNull(index)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable { focusRequester.requestFocus() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (char.isNotEmpty()) "•" else "",
                            fontSize = 40.sp,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.popBackStack() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text(
                    text = "Resend code ",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(y = (-1.8).dp)
                )
            }
        }
    }
}

