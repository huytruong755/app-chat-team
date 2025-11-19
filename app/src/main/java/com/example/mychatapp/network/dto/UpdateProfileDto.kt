package com.example.mychatapp.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO cho Auth/update-profile
 * Khớp với UpdateProfileDTO từ backend
 */
data class UpdateProfileDto(
    @SerializedName("FirstName")
    val firstName: String,

    @SerializedName("LastName")
    val lastName: String,

    @SerializedName("AvatarUrl")
    val avatarUrl: String
)

