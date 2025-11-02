package com.example.mychatapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.data.sampleChats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ================================
 * 🧠 ChatViewModel.kt
 * ================================
 * Trách nhiệm:
 * - Quản lý dữ liệu danh sách chat.
 * - Hiện tại load dữ liệu giả (sampleChats).
 * - Sau này sẽ gọi API thật khi backend hoàn tất.
 *
 * BACKEND (ASP.NET C#):
 * - Cần cung cấp API:
 *      GET /api/chat               → trả về danh sách đoạn chat (gần nhất)
 *      GET /api/chat/{friendId}    → trả về lịch sử tin nhắn
 * - Và Hub SignalR:
 *      /chathub                   → nhận/gửi tin nhắn real-time
 */

class ChatViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            // 👉 TODO: Khi backend đã sẵn sàng:
            // val realChats = apiService.getChats(token)
            // _chats.value = realChats

            // 🔹 Tạm dùng dữ liệu mẫu
            _chats.value = sampleChats
        }
    }
}
