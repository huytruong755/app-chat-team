package com.example.mychatapp.model.modelData

/**
 * Chat model - Khớp với ChatResponseDto từ backend
 * Được map từ ChatResponseDto (Chat + Info)
 */
data class Chat(
    val id: Int,
    val name: String, // FullName từ PartnerInfoDto
    val lastMessage: String,
    val time: String, // LastMessageTime từ ChatInfoDto
    val avatarUrl: String?,
    val unreadCount: Int = 0,
    val isOnline: Boolean? = false,
    val partnerId: Int? = null // ID của người đối thoại
)