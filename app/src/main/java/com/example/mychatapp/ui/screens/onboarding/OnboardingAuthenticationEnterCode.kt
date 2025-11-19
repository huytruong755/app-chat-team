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
import com.example.mychatapp.network.RetrofitInstance // 💡 SỬA IMPORT
import com.example.mychatapp.network.dto.LoginRequestDto
//import com.example.mychatapp.network.dto.LoginResponseDto
import com.example.mychatapp.ui.screens.onboarding.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
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
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val apiService = RetrofitInstance.api

    LaunchedEffect(otpCode) {
        if (otpCode.length == maxLength) {
            isLoading = true
            errorMessage = ""
            android.util.Log.d("OnboardingAuth", "OTP code entered: $otpCode")
            
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)

            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    android.util.Log.d("OnboardingAuth", "Firebase auth result: ${task.isSuccessful}")
                    if (task.isSuccessful) {
                        Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                        android.util.Log.d("OnboardingAuth", "Calling API login with phone: $phoneNumber")

                        coroutineScope.launch {
                            try {
                                android.util.Log.d("OnboardingAuth", "API call started to: http://192.168.1.39:5047/Auth/login")
                                val response = apiService.login(
                                    LoginRequestDto(phoneNumber = phoneNumber)
                                )
                                android.util.Log.d("OnboardingAuth", "API response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

                                if (response.isSuccessful) {
                                    // === KỊCH BẢN 1: USER CŨ ===
                                    val loginResponse = response.body()
                                    if (loginResponse != null) {
                                        // Cập nhật currentUserId
                                        com.example.mychatapp.model.viewModel.ContactRepository.currentUserId = loginResponse.userId
                                        com.example.mychatapp.model.viewModel.ContactRepository.setAuthToken(loginResponse.accessToken)
                                        
                                        sessionManager.saveUserSession(
                                            id = loginResponse.userId.toString(),
                                            name = "",
                                            token = loginResponse.accessToken,
                                            avatarUrl = ""
                                        )
                                        Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                        navController.navigate("mainScreen") {
                                            popUpTo("PhoneNumber") { inclusive = true }
                                        }
                                    } else {
                                        isLoading = false
                                        errorMessage = "API Error: Empty response body"
                                    }
                                } else if (response.code() == 401) {
                                    // === KỊCH BẢN 2: USER MỚI ===
                                    Toast.makeText(context, "New user! Please create profile.", Toast.LENGTH_SHORT).show()
                                    isLoading = false

                                    navController.navigate("UserProfile/$phoneNumber") {
                                        popUpTo("PhoneNumber") { inclusive = true }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "API Error: ${response.code()} ${response.message()}"
                                }
                            } catch (e: java.net.ConnectException) {
                                isLoading = false
                                android.util.Log.e("OnboardingAuth", "ConnectException: ${e.message}", e)
                                errorMessage = "⚠️ Không thể kết nối đến server!\n\n" +
                                        "Vui lòng kiểm tra:\n" +
                                        "• Server đã chạy chưa? (http://192.168.1.39:5047)\n" +
                                        "• IP address đúng chưa?\n" +
                                        "• Điện thoại và máy tính cùng mạng WiFi?\n" +
                                        "• Firewall có chặn port 5047 không?\n\n" +
                                        "Chi tiết: ${e.message}"
                            } catch (e: java.net.SocketTimeoutException) {
                                isLoading = false
                                errorMessage = "⏱️ Kết nối quá lâu!\n\n" +
                                        "Server có thể đang quá tải hoặc không phản hồi.\n" +
                                        "Vui lòng thử lại sau."
                            } catch (e: java.net.UnknownHostException) {
                                isLoading = false
                                errorMessage = "🌐 Không tìm thấy server!\n\n" +
                                        "Không thể phân giải địa chỉ IP.\n" +
                                        "Vui lòng kiểm tra kết nối mạng."
                            } catch (e: Exception) {
                                isLoading = false
                                // Xử lý các loại lỗi khác nhau
                                val errorMsg = when {
                                    e.message?.contains("failed to connect", ignoreCase = true) == true -> 
                                        "⚠️ Không thể kết nối đến server!\n\n" +
                                        "Vui lòng kiểm tra:\n" +
                                        "• Server đã chạy chưa? (http://192.168.1.39:5047)\n" +
                                        "• IP address đúng chưa?\n" +
                                        "• Điện thoại và máy tính cùng mạng WiFi?"
                                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                                        "⏱️ Kết nối quá lâu. Vui lòng thử lại."
                                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true -> 
                                        "🌐 Không tìm thấy server. Vui lòng kiểm tra kết nối mạng."
                                    else -> "❌ Lỗi: ${e.message ?: "Không xác định"}\n\n" +
                                            "Chi tiết: ${e.javaClass.simpleName}"
                                }
                                errorMessage = errorMsg
                                android.util.Log.e("OnboardingAuth", "Login error", e)
                            }
                        }
                    } else {
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
                    IconButton(onClick = { navController.popBackStack() }) { // Quay lại
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

            // Lặp 6 ô
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Button để test API trực tiếp (bypass Firebase) - dùng để debug khi server đã chạy
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = ""
                        try {
                            android.util.Log.d("OnboardingAuth", "Testing API directly (bypass Firebase)...")
                            android.util.Log.d("OnboardingAuth", "Phone number: $phoneNumber")
                            android.util.Log.d("OnboardingAuth", "API URL: http://192.168.1.39:5047/Auth/login")
                            
                            val response = apiService.login(
                                LoginRequestDto(phoneNumber = phoneNumber)
                            )
                            
                            android.util.Log.d("OnboardingAuth", "Test API response code: ${response.code()}")
                            android.util.Log.d("OnboardingAuth", "Test API response message: ${response.message()}")
                            android.util.Log.d("OnboardingAuth", "Test API isSuccessful: ${response.isSuccessful}")
                            
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                android.util.Log.d("OnboardingAuth", "Login response body: $loginResponse")
                                if (loginResponse != null) {
                                    com.example.mychatapp.model.viewModel.ContactRepository.currentUserId = loginResponse.userId
                                    com.example.mychatapp.model.viewModel.ContactRepository.setAuthToken(loginResponse.accessToken)
                                    sessionManager.saveUserSession(
                                        id = loginResponse.userId.toString(),
                                        name = "",
                                        token = loginResponse.accessToken,
                                        avatarUrl = ""
                                    )
                                    Toast.makeText(context, "✅ Test login successful!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("mainScreen") {
                                        popUpTo("PhoneNumber") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = "❌ Test API: Response body is null"
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                android.util.Log.e("OnboardingAuth", "Test API error body: $errorBody")
                                errorMessage = "❌ Test API Error:\n" +
                                        "Code: ${response.code()}\n" +
                                        "Message: ${response.message()}\n" +
                                        "Error: ${errorBody ?: "No error body"}"
                            }
                        } catch (e: java.net.ConnectException) {
                            android.util.Log.e("OnboardingAuth", "Test API ConnectException", e)
                            errorMessage = "⚠️ Không thể kết nối!\n\n" +
                                    "Server có thể chưa chạy.\n" +
                                    "Vui lòng chạy backend server trước.\n\n" +
                                    "Chi tiết: ${e.message}"
                        } catch (e: java.net.SocketTimeoutException) {
                            android.util.Log.e("OnboardingAuth", "Test API SocketTimeoutException", e)
                            errorMessage = "⏱️ Timeout!\n\n" +
                                    "Server không phản hồi trong 15 giây.\n" +
                                    "Vui lòng kiểm tra server."
                        } catch (e: Exception) {
                            android.util.Log.e("OnboardingAuth", "Test API error", e)
                            errorMessage = "❌ Test API Error:\n${e.message}\n\n" +
                                    "Type: ${e.javaClass.simpleName}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "🔧 Test API (Bypass Firebase)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = { navController.popBackStack() }, // Quay về để gửi lại
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
            
            // Loading indicator
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF1E88E5)
                )
            }
        }
    }
}