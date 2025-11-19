package com.example.mychatapp.model.viewModel

import android.util.Log
import com.example.mychatapp.model.modelData.Chat
import com.example.mychatapp.model.modelData.ChatMessage
import com.example.mychatapp.model.modelData.Contact
import com.example.mychatapp.network.RetrofitInstance
import com.example.mychatapp.network.SignalRService
import com.example.mychatapp.network.dto.ChatResponseDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object ChatRepository {

    private val api = RetrofitInstance.api
    private const val BASE_URL = "http://192.168.1.39:5047"

    private var currentUserId: Int = 0
    private var authToken: String = ""

    // === DANH SÁCH CHAT ===
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    // === MESSAGES ===
    private val _messages = mutableMapOf<String, MutableStateFlow<List<ChatMessage>>>()

    // Map FriendId -> ChatId (server)
    private val friendToChatIdMap = mutableMapOf<Int, Int>()

    init {
        // Lắng nghe realtime message từ SignalR
        CoroutineScope(Dispatchers.IO).launch {
            SignalRService.incomingMessages.collect { msgDto ->
                // Chuyển DTO -> UI model
                val friendId = friendToChatIdMap.entries.find { it.value == (msgDto.chatId ?: 0) }?.key
                if (friendId != null && msgDto.chatId != null) {
                    val newMessage = ChatMessage(
                        id = msgDto.id,
                        chatId = msgDto.chatId,
                        senderId = msgDto.senderId,
                        senderName = msgDto.senderName,
                        content = msgDto.content,
                        fileUrl = msgDto.fileUrl?.let { if (it.startsWith("http")) it else "$BASE_URL$it" },
                        fileType = msgDto.fileType ?: "text",
                        timestamp = msgDto.sentTime,
                        status = msgDto.status ?: "sent",
                        isSentByMe = msgDto.senderId == currentUserId
                    )
                    updateMessageList(friendId.toString(), newMessage)
                } else {
                    Log.e("ChatRepo", "Cannot map incoming message to any friendId. chatId: ${msgDto.chatId}")
                }
            }
        }
    }

    fun initialize(token: String, myId: Int) {
        currentUserId = myId
        authToken = token
        CoroutineScope(Dispatchers.IO).launch {
            SignalRService.startConnection(token)
        }
    }

    // Lấy messages theo FriendId
    fun getMessagesForChat(friendId: String): MutableStateFlow<List<ChatMessage>> {
        if (!_messages.containsKey(friendId)) _messages[friendId] = MutableStateFlow(emptyList())
        return _messages[friendId]!!
    }

    // Cập nhật message list + last message
    private fun updateMessageList(friendId: String, message: ChatMessage) {
        val flow = getMessagesForChat(friendId)
        val currentList = flow.value.toMutableList()
        currentList.add(0, message)  // add mới lên đầu
        flow.value = currentList

        updateLastMessage(friendId, message)
    }

    // Fetch lịch sử tin nhắn từ server
    fun fetchMessages(chatId: Int, friendIdString: String) {
        val friendId = friendIdString.toIntOrNull() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getMessages("Bearer $authToken", chatId)
                if (response.isSuccessful && response.body() != null) {
                    val messages = response.body()!!

                    // Lưu ChatId từ server
                    friendToChatIdMap[friendId] = chatId

                    // Join SignalR group
                    SignalRService.joinChatGroup(chatId.toString())

                    // Map DTO -> UI
                    val uiMessages = messages.map { msgDto ->
                        ChatMessage(
                            id = msgDto.id,
                            chatId = chatId,
                            senderId = msgDto.senderId,
                            senderName = msgDto.senderName,
                            content = msgDto.content,
                            fileUrl = msgDto.fileUrl?.let { if (it.startsWith("http")) it else "$BASE_URL$it" },
                            fileType = msgDto.fileType ?: "text",
                            timestamp = msgDto.sentTime,
                            status = msgDto.status ?: "sent",
                            isSentByMe = msgDto.senderId == currentUserId
                        )
                    }
                    getMessagesForChat(friendIdString).value = uiMessages
                }
            } catch (e: Exception) {
                Log.e("ChatRepo", "Error fetching messages: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // === Gửi tin nhắn text ===
    fun sendTextMessage(senderId: String, friendIdString: String, text: String) {
        val senderIdInt = senderId.toIntOrNull() ?: return
        val friendIdInt = friendIdString.toIntOrNull() ?: return
        val realChatId = friendToChatIdMap[friendIdInt]

        // Optimistic update
        val tempMessage = ChatMessage(
            id = System.currentTimeMillis().toInt(),
            chatId = realChatId,
            senderId = senderIdInt,
            senderName = "You",
            content = text,
            fileUrl = null,
            fileType = "text",
            timestamp = "Sending...",
            status = "sending",
            isSentByMe = true
        )
        updateMessageList(friendIdString, tempMessage)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val chatIdBody = realChatId?.toString()?.toRequestBody() ?: null
                val senderIdBody = senderIdInt.toString().toRequestBody()
                val receiverIdBody = friendIdInt.toString().toRequestBody()
                val fileTypeBody = "text".toRequestBody()
                val contentBody = text.toRequestBody()

                val response = api.sendMessage(
                    token = "Bearer $authToken",
                    chatId = chatIdBody,
                    senderId = senderIdBody,
                    receiverId = receiverIdBody,
                    fileType = fileTypeBody,
                    content = contentBody,
                    file = null
                )

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        // Cập nhật chatId nếu chưa có
                        if (realChatId == null) {
                            friendToChatIdMap[friendIdInt] = result.chatId
                            SignalRService.joinChatGroup(result.chatId.toString())
                        }
                        Log.d("ChatRepo", "Message sent successfully")
                    }
                } else {
                    Log.e("ChatRepo", "Failed to send message: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatRepo", "Error sending message: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // === Gửi tin nhắn hình ===
    fun sendImageMessage(senderId: Int, friendId: Int, imageFile: File) {
        val realChatId = friendToChatIdMap[friendId]

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val chatIdBody = realChatId?.toString()?.toRequestBody() ?: null
                val senderIdBody = senderId.toString().toRequestBody()
                val receiverIdBody = friendId.toString().toRequestBody()
                val fileTypeBody = "image".toRequestBody()
                val contentBody = "".toRequestBody()

                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("File", imageFile.name, requestFile)

                val response = api.sendMessage(
                    token = "Bearer $authToken",
                    chatId = chatIdBody,
                    senderId = senderIdBody,
                    receiverId = receiverIdBody,
                    fileType = fileTypeBody,
                    content = contentBody,
                    file = filePart
                )

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        if (realChatId == null) {
                            friendToChatIdMap[friendId] = result.chatId
                            SignalRService.joinChatGroup(result.chatId.toString())
                        }
                        Log.d("ChatRepo", "Image sent successfully")
                    }
                } else {
                    Log.e("ChatRepo", "Failed to send image: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatRepo", "Error sending image: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Cập nhật tin nhắn cuối cùng của chat
    private fun updateLastMessage(friendId: String, message: ChatMessage) {
        val currentChats = _chats.value.toMutableList()
        val friendIdInt = friendId.toIntOrNull() ?: return
        val chatIndex = currentChats.indexOfFirst { it.partnerId == friendIdInt }

        if (chatIndex != -1) {
            val oldChat = currentChats[chatIndex]
            val lastMsgText = when {
                message.content != null -> message.content
                message.fileType == "image" -> "📷 Hình ảnh"
                message.fileType == "video" -> "🎥 Video"
                else -> "[File]"
            }
            val updatedChat = oldChat.copy(
                lastMessage = lastMsgText,
                time = message.timestamp
            )
            currentChats.removeAt(chatIndex)
            currentChats.add(0, updatedChat)
            _chats.value = currentChats
        } else {
            fetchChats()
        }
    }

    fun fetchChats() {
        if (authToken.isEmpty()) {
            Log.e("ChatRepo", "Auth token is empty, cannot fetch chats")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getChatConversations("Bearer $authToken")
                if (response.isSuccessful && response.body() != null) {
                    val chatDtos = response.body()!!
                    val chats = chatDtos.map { dto ->
                        // Lưu mapping friendId -> chatId
                        val partnerId = dto.info.id
                        friendToChatIdMap[partnerId] = dto.chat.id

                        Chat(
                            id = dto.chat.id,
                            name = dto.info.fullName,
                            lastMessage = dto.chat.lastMessage,
                            time = dto.chat.lastMessageTime,
                            avatarUrl = dto.info.avatarUrl?.let { if (it.startsWith("http")) it else "$BASE_URL$it" },
                            unreadCount = dto.chat.unreadCount,
                            isOnline = dto.info.isOnline ?: false,
                            partnerId = partnerId
                        )
                    }
                    _chats.value = chats
                } else {
                    Log.e("ChatRepo", "Failed to fetch chats: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatRepo", "Error fetching chats: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun addChat(friend: Contact) {
        val currentChats = _chats.value.toMutableList()
        if (currentChats.none { it.id == friend.id }) {
            val newChat = Chat(
                id = friend.id,
                name = friend.name,
                lastMessage = "Say hello 👋",
                time = "Now",
                avatarUrl = friend.avatarUrl
            )
            currentChats.add(newChat)
            _chats.value = currentChats

            if (!_messages.containsKey(friend.id.toString())) {
                _messages[friend.id.toString()] = MutableStateFlow(emptyList())
            }
        }
    }

    fun clearData() {
        _chats.value = emptyList()
        _messages.clear()
        friendToChatIdMap.clear()
        currentUserId = 0
        SignalRService.stopConnection()
    }
}
