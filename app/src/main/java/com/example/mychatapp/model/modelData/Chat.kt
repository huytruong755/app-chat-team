package com.example.mychatapp.model.modelData

/**
 * ================================
 * Chat.kt
 * ================================
 * Cấu trúc dữ liệu mô tả 1 đoạn chat.
 *
 * BACKEND (C# API):
 * Khi gọi GET /api/chat → trả về JSON có dạng:
 * [
 *   {
 *     "id": "1",
 *     "name": "Athalia Putri",
 *     "lastMessage": "Hey!",
 *     "time": "09:45 AM",
 *     "avatarUrl": "https://..."
 *   }
 * ]
 */
data class Chat(
    val id: String,
    val name: String,
    val lastMessage: String,
    val time: String,
    val avatarUrl: String?
)
