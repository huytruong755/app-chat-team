package com.example.mychatapp.ui.screens.onboarding

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mychatapp.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationEnterCode(
    navController: NavController,
    phoneNumber: String
) {
    var otpCode by remember { mutableStateOf("") }
    val maxLength = 4

    var isSending by remember { mutableStateOf(false) }
    var resendMessage by remember { mutableStateOf("") }
    var shouldResend by remember { mutableStateOf(false) }
    val userInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    //giả lập otp backend gửi về
    val correctOtpCode = "1234"

    LaunchedEffect(otpCode) {
        if (otpCode.length == 4) {
            if (otpCode == correctOtpCode) {
                delay(300) // hiệu ứng nhỏ
                navController.navigate("UserProfile") {
                    popUpTo("AuthenticationEnterCode/{phoneNumber}") { inclusive = true }
                }
            } else {
                errorMessage = "Mã OTP không khả dụng, vui lòng thử lại!"
            }
        }
    }

    // Giả lập gửi lại mã OTP
    LaunchedEffect(shouldResend) {
        if (shouldResend) {
            isSending = true
            resendMessage = "Đang gửi lại mã..."
            delay(2000)
            isSending = false
            resendMessage = "Đã gửi lại mã OTP!"
            shouldResend = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("PhoneNumber") }) {
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

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { shouldResend = true },
                enabled = !isSending,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text(
                    text = if (isSending) "Đang gửi..." else "Resend code",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(y = (-1.8).dp)
                )
            }

            if (resendMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resendMessage,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
