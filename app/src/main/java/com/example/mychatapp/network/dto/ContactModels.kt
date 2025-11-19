package com.example.mychatapp.network.dto

import com.google.gson.annotations.SerializedName

/**
 * UserDto - Khớp với User model từ backend
 * Response từ User/list và User/{id}
 */
data class UserDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("avatarUrl")
    val avatarUrl: String?,

    @SerializedName("isOnline")
    val isOnline: Boolean?,

    @SerializedName("lastSeen")
    val lastSeen: String?
)

/**
 * ContactResponseWrapper - Khớp với response từ ContactController
 * Response: { userId: int, contacts: List<{ Id: int, contactInfo: {...} }> }
 */
data class ContactResponseWrapper(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("contacts")
    val contacts: List<ContactItemDto>
)

data class ContactItemDto(
    @SerializedName("Id")
    val relationshipId: Int, // ID của bảng Contact

    @SerializedName("contactInfo")
    val contactInfo: ContactUserInfoDto // Thông tin người bạn
)

/**
 * ContactUserInfoDto - Khớp với contactInfo trong response
 * Backend trả về: { Id, FullName, isOnline, UserAva }
 */
data class ContactUserInfoDto(
    @SerializedName("Id")
    val id: Int,

    @SerializedName("FullName")
    val fullName: String,

    @SerializedName("isOnline")
    val isOnline: Boolean?,

    @SerializedName("UserAva")
    val avatarUrl: String?
)