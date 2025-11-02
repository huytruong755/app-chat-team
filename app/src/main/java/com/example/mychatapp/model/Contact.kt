package com.example.mychatapp.model

data class Contact(
    val id: String,
    val name: String,
    val status: String,
    val isOnline: Boolean,
    val avatarUrl: String?
)
data class ChatConversation(
    val id: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int
)