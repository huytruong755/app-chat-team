package com.example.mychatapp.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.model.modelData.Chat
import com.example.mychatapp.model.modelData.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<Contact>>(emptyList())
    val chats: StateFlow<List<Chat>> = ChatRepository.chats

    init {
        viewModelScope.launch {
            ChatRepository.fetchChats()
        }
    }

    fun refreshChats() {
        viewModelScope.launch {
            ChatRepository.fetchChats()
        }
    }

    fun addChat(friend: Contact) {
        viewModelScope.launch {
            ChatRepository.addChat(friend)
        }
    }

    /**
     * ✅ Load lịch sử chat theo FriendId
     * LƯU Ý: Cần chatId từ server, nên cần fetch chats trước để lấy chatId
     */
    fun loadChatHistoryForFriend(chatId: Int, friendId: String) {
        viewModelScope.launch {
            ChatRepository.fetchMessages(chatId, friendId)
        }
    }
}
