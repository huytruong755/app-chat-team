package com.example.mychatapp.model.modelData

/**
 * ChatMessage - Khớp với MessageResponseDto từ backend
 * Được map từ MessageResponseDto
 */
data class ChatMessage(
    val id: Int,
    val chatId: Int? = null, // Có thể null nếu lấy từ GetMessages
    val senderId: Int,
    val senderName: String,
    val content: String?,
    val fileUrl: String?,
    val fileType: String = "text", // "text", "image", "video", etc.
    val timestamp: String, // sentTime từ backend
    val status: String = "sent", // "sent", "delivered", "read"
    val isSentByMe: Boolean = false // Tính toán dựa trên senderId vs currentUserId
)