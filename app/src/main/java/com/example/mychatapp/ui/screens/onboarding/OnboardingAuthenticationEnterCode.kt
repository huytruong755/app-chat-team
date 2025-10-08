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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

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

    // ⚡ Biến tạm để chỉnh số điện thoại (nếu cần)
    var phoneInput by remember { mutableStateOf(phoneNumber) }

    // Focus requester để hiển thị bàn phím khi nhấn vào ô
    val focusRequester = remember { FocusRequester() }

    // Giả lập gửi lại mã
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

    Column(
        modifier = Modifier
            .padding(16.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Enter Code",
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF000000),
            lineHeight = 38.sp,
            modifier = Modifier.width(350.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "We have sent you an SMS with the code\n to $phoneNumber",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF000000),
            lineHeight = 28.sp,
            modifier = Modifier.width(350.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // ✅ TextField ẩn - nhưng có thể focus để hiện bàn phím
        TextField(
            value = otpCode,
            onValueChange = {
                if (it.length <= maxLength && it.all { c -> c.isDigit() }) {
                    otpCode = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        .clickable {
                            // Khi nhấn vào ô -> focus vào TextField ẩn -> bàn phím hiện
                            focusRequester.requestFocus()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
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
            Text(text = resendMessage, color = Color.Gray, fontSize = 14.sp)
        }
    }
}
