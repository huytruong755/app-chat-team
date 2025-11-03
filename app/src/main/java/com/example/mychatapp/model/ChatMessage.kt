package com.example.mychatapp.model

/**
 * ====================================
 * ChatMessage.kt
 * ====================================
 * Mô tả từng tin nhắn trong khung hội thoại.
 *
 * BACKEND (C# API):
 * Khi gọi GET /api/messages/{conversationId} → trả về JSON:
 * [
 *   {
 *     "id": "101",
 *     "senderId": "1",
 *     "receiverId": "2",
 *     "type": "text", // hoặc "image"
 *     "content": "Xin chào!",
 *     "imageUrl": null,
 *     "timestamp": "2025-11-03T09:45:00Z",
 *     "isRead": true
 *   }
 * ]
 */

data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val type: String,          // "text" hoặc "image"
    val content: String?,      // nội dung tin nhắn text
    val imageUrl: String?,     // đường dẫn ảnh (nếu có)
    val timestamp: String,     // thời gian gửi
    val isRead: Boolean,       // đã đọc hay chưa
    val isSentByMe: Boolean    // để UI biết hiển thị bên trái hay phải
)
