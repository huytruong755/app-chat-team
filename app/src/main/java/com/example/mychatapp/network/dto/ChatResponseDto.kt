package com.example.mychatapp.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Response từ ChatController.GetConverById()
 * Trả về List<{ Chat: {...}, Info: {...} }>
 */
data class ChatResponseDto(
    @SerializedName("Chat")
    val chat: ChatInfoDto,

    @SerializedName("Info")
    val info: PartnerInfoDto
)

data class ChatInfoDto(
    @SerializedName("Id")
    val id: Int,

    @SerializedName("LastMessage")
    val lastMessage: String,

    @SerializedName("LastMessageTime")
    val lastMessageTime: String,

    @SerializedName("UnreadCount")
    val unreadCount: Int
)

data class PartnerInfoDto(
    @SerializedName("Id")
    val id: Int,

    @SerializedName("FullName")
    val fullName: String,

    @SerializedName("AvatarUrl")
    val avatarUrl: String?,

    @SerializedName("isOnline")
    val isOnline: Boolean?,

    @SerializedName("lastSeen")
    val lastSeen: String?
)

