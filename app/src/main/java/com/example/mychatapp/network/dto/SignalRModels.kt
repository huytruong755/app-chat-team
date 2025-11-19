package com.example.mychatapp.network.dto

import com.google.gson.annotations.SerializedName

/**
 * MessageResponseDto - Khớp với response từ MessageController
 * 
 * Response từ Message/{ChatId}:
 * - id, senderId, senderName, content, fileUrl, fileType, sentTime
 * - KHÔNG có chatId và status
 * 
 * Response từ SignalR ReceiveMessage (MessageDto):
 * - id, chatId, senderId, senderName, content, fileUrl, fileType, sentTime, status
 */
data class MessageResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("chatId")
    val chatId: Int? = null, // Có trong SignalR, không có trong GetMessages

    @SerializedName("senderId")
    val senderId: Int,

    @SerializedName("senderName")
    val senderName: String,

    @SerializedName("content")
    val content: String?,

    @SerializedName("fileUrl")
    val fileUrl: String?,

    @SerializedName("fileType")
    val fileType: String? = "text", // Có thể null trong GetMessages

    @SerializedName("sentTime")
    val sentTime: String,

    @SerializedName("status")
    val status: String? = null // Có trong SignalR, không có trong GetMessages
)