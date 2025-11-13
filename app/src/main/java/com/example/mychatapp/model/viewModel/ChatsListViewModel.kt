package com.example.mychatapp.model.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.model.modelData.ChatMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private var messageCollectionJob: Job? = null

    // Biến này để lưu ID của chat đang xem
    private var currentChatId: String? = null

    /**
     * Gửi tin nhắn text
     */
    fun sendTextMessage(senderId: String, receiverId: String, text: String) {
        viewModelScope.launch {
            ChatRepository.sendTextMessage(senderId, receiverId, text)

            // Sau khi gửi, chúng ta "ép" ViewModel
            // hủy luồng cũ và tải lại luồng mới
            currentChatId?.let {
                loadChatHistory(it)
            }
        }
    }

    /**
     * ✅ Gửi tin nhắn hình
     */
    fun uploadImageMessage(localUri: Uri, senderId: String, receiverId: String) {
        viewModelScope.launch {
            val uriString = localUri.toString()
            ChatRepository.sendImageMessage(senderId, receiverId, uriString)

            currentChatId?.let {
                loadChatHistory(it)
            }
        }
    }

    /**
     * ✅ Lấy lịch sử chat
     */
    fun loadChatHistory(receiverId: String) {
        // Lưu lại ID của chat đang xem
        currentChatId = receiverId

        // Hủy bỏ bất kỳ công việc thu thập tin nhắn cũ nào
        messageCollectionJob?.cancel()

        // Bắt đầu một công việc mới và lưu nó
        messageCollectionJob = ChatRepository.getMessagesForChat(receiverId)
            .onEach { messageList ->
                // Chỉ cập nhật tin nhắn từ luồng MỚI
                _messages.value = messageList
            }
            .launchIn(viewModelScope) // Chạy công việc
    }
}