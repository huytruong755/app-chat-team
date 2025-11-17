package com.example.mychatapp.network.dto

/**
 * DTO (Data Transfer Object)
 * Nhận Token tùy chỉnh (C#) TỪ server.
 */
data class LoginResponseDto(
    val token: String,
    val userId: String,
    val name: String
    // Thêm bất kỳ thông tin nào khác mà C# trả về khi đăng nhập
)