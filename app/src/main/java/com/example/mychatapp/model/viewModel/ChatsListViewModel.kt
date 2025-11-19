package com.example.mychatapp.model.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.model.modelData.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Biến lưu chat hiện tại
    private var currentChatId: String? = null

    fun sendTextMessage(senderId: String, receiverId: String, text: String) {
        viewModelScope.launch {
            ChatRepository.sendTextMessage(senderId, receiverId, text)
            // Note: loadChatHistory needs chatId and friendId, cannot call here without chatId
        }
    }

    fun uploadImageMessage(context: Context, localUri: Uri, senderId: Int, receiverId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Tạo file tạm từ URI
                val inputStream = context.contentResolver.openInputStream(localUri)
                val tempFile = java.io.File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                inputStream?.close()
                
                if (tempFile.exists()) {
                    ChatRepository.sendImageMessage(senderId, receiverId, tempFile)
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatListViewModel", "Error uploading image: ${e.message}")
            }
        }
    }

    fun loadChatHistory(chatId: Int, friendId: String) {
        currentChatId = friendId

        viewModelScope.launch {
            // 1. 🔥 BẮT BUỘC: Gọi lệnh tải dữ liệu từ Server về
            ChatRepository.fetchMessages(chatId, friendId)

            // 2. Lắng nghe dữ liệu từ Repository (để cập nhật UI Realtime)
            ChatRepository.getMessagesForChat(friendId).collect { list ->
                _messages.value = list
            }
        }
    }
}
