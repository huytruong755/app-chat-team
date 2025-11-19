package com.example.mychatapp.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object)
 * Nhận Token từ backend AppChat
 * Response từ Auth/login: { accessToken: string, userId: int }
 */
data class LoginResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("userId")
    val userId: Int
)