package com.example.mychatapp.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Response từ MessageController.SendMessage()
 * Trả về: { chatId: int, message: MessageDto }
 */
data class SendMessageResponseDto(
    @SerializedName("chatId")
    val chatId: Int,

    @SerializedName("message")
    val message: MessageResponseDto
)

