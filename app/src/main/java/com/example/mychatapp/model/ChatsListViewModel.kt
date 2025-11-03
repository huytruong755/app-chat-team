package com.example.mychatapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * =====================================
 * ChatListViewModel.kt
 * =====================================
 * Quản lý chi tiết 1 cuộc hội thoại (giữa 2 user)
 *
 * FRONTEND:
 * - Gọi trong OnboardingChatDetail.kt
 * BACKEND (C# API):
 * - GET /api/messages/history/{senderId}/{receiverId}
 * - POST /api/messages/send-text
 * - POST /api/messages/send-image (multipart/form-data)
 * - SignalR Hub /chatHub cho real-time
 */

class ChatListViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    /**
     * ✅ Gửi tin nhắn text
     * TODO: Thay bằng API thật sau khi backend triển khai
     */
    fun sendTextMessage(senderId: String, receiverId: String, text: String) {
        viewModelScope.launch {
            val newMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                senderId = senderId,
                receiverId = receiverId,
                type = "text",
                content = text,
                imageUrl = null,
                timestamp = System.currentTimeMillis().toString(),
                isRead = false,
                isSentByMe = true
            )

            // ⚙️ [THAY BẰNG API THẬT]:
            // val response = api.sendTextMessage(SendTextRequest(senderId, receiverId, text))
            // _messages.value = _messages.value + response.body()
            _messages.value = listOf(newMessage) + _messages.value
        }
    }

    /**
     * ✅ Upload ảnh và gửi tin nhắn hình
     * TODO: Sử dụng multipart/form-data với file ảnh
     */
    fun uploadImageMessage(localUri: String, senderId: String, receiverId: String) {
        viewModelScope.launch {
            val newImageMsg = ChatMessage(
                id = System.currentTimeMillis().toString(),
                senderId = senderId,
                receiverId = receiverId,
                type = "image",
                content = null,
                imageUrl = localUri, // thay bằng URL backend trả về
                timestamp = System.currentTimeMillis().toString(),
                isRead = false,
                isSentByMe = true
            )

            // ⚙️ [THAY BẰNG API THẬT]:
            // val filePart = MultipartBody.Part.createFormData(...)
            // val response = api.sendImageMessage(filePart)
            _messages.value = listOf(newImageMsg) + _messages.value
        }
    }

    /**
     * ✅ Lấy lịch sử chat khi mở khung
     */
    suspend fun loadChatHistory(senderId: String, receiverId: String) {
        withContext(Dispatchers.IO) {
            // ⚙️ [THAY BẰNG API THẬT]:
            // val response = api.getHistory(senderId, receiverId)
            // _messages.value = response.body() ?: emptyList()
        }
    }
}
