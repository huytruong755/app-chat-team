package com.example.mychatapp.model.viewModel

import com.example.mychatapp.data.sampleChats
import com.example.mychatapp.model.modelData.Chat
import com.example.mychatapp.model.modelData.ChatMessage
import com.example.mychatapp.model.modelData.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ================================
 * 📦 ChatRepository.kt (Singleton)
 * ================================
 * Đây là "Nguồn sự thật chung" (Single Source of Truth)
 * thay thế cho database/API khi dùng dữ liệu mẫu.
 *
 * Nó là một 'object' (singleton) để cả 2 ViewModel
 * có thể truy cập và chia sẻ cùng một dữ liệu.
 */
object ChatRepository {

    // == PHẦN 1: DANH SÁCH CHAT ==

    // Giữ danh sách chat, khởi tạo bằng dữ liệu mẫu
    private val _chats = MutableStateFlow(sampleChats)
    val chats = _chats.asStateFlow() // Expose ra ngoài

    // Dữ liệu mẫu cho tin nhắn chi tiết
    // Key = ID của cuộc hội thoại (giống ID của Chat)
    private val sampleMessages = mutableMapOf(
        "1" to mutableListOf(
            ChatMessage("msg1_1", "1", "user_self", "text", "Hey, how are you?", null, "09:45 AM",
                isRead = false,
                isSentByMe = false
            )
        ),
        "2" to mutableListOf(
            ChatMessage("msg2_1", "2", "user_self", "text", "Let's meet tomorrow!", null, "Yesterday",
                isRead = false,
                isSentByMe = false
            )
        ),
        "3" to mutableListOf(
            ChatMessage("msg3_1", "3", "user_self", "text", "Typing...", null, "Now",
                isRead = false,
                isSentByMe = false
            )
        )
    )

    // Giữ tất cả tin nhắn
    private val _messages = MutableStateFlow(sampleMessages)

    /**
     * Lấy Flow tin nhắn cho một chat cụ thể
     */
    fun getMessagesForChat(chatId: String): MutableStateFlow<List<ChatMessage>> {
        // Đảm bảo rằng có một danh sách tin nhắn cho chatId này
        if (!_messages.value.containsKey(chatId)) {
            _messages.value[chatId] = mutableListOf()
        }
        // Chúng ta cần một cách để trả về một Flow chỉ cho list này.
        // Đây là một cách đơn giản hóa:
        // Trong 1 app thật, bạn sẽ query từ DB (ví dụ: Room)
        // Ở đây chúng ta sẽ trả về 1 flow mới chỉ chứa list đó
        return MutableStateFlow(_messages.value[chatId] ?: emptyList())
        // Tốt hơn: Trả về 1 flow mà cập nhật khi map thay đổi
        // (Nhưng để đơn giản, chúng ta sẽ cập nhật _messages và _chats)
    }

    // == PHẦN 3: HÀNH ĐỘNG (GHI DỮ LIỆU) ==

    /**
     * Gửi tin nhắn mới.
     * Đây là hàm quan trọng nhất.
     */
    fun sendTextMessage(senderId: String, receiverId: String, text: String) {
        // 1. Tạo tin nhắn mới
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = senderId,
            receiverId = receiverId,
            type = "text",
            content = text,
            imageUrl = null,
            timestamp = System.currentTimeMillis().toString(),
            isRead = false,
            isSentByMe = true // Giả sử người gửi là 'tôi'
        )

        // 2. Thêm tin nhắn này vào danh sách tin nhắn chi tiết
        val currentMessages = _messages.value[receiverId]?.toMutableList() ?: mutableListOf()
        currentMessages.add(0, newMessage) // Thêm lên đầu (hoặc cuối, tùy UI)
        _messages.value[receiverId] = currentMessages

        // 3. CẬP NHẬT 'lastMessage' TRONG DANH SÁCH CHAT CHÍNH
        val currentChats = _chats.value.toMutableList()
        val chatIndex = currentChats.indexOfFirst { it.id == receiverId }

        if (chatIndex != -1) {
            val oldChat = currentChats[chatIndex]
            val updatedChat = oldChat.copy(
                lastMessage = text, // Cập nhật tin nhắn cuối
                time = SimpleDateFormat("HH:mm a", Locale.US).format(Date()) // Cập nhật thời gian
            )
            currentChats[chatIndex] = updatedChat
            _chats.value = currentChats // Phát ra danh sách chat đã cập nhật
        }
    }

    /**
     * Thêm một cuộc hội thoại mới (từ màn hình AddFriend)
     */
    fun addChat(friend: Contact) {
        val updatedChats = _chats.value.toMutableList()
        if (updatedChats.none { it.id == friend.id }) {
            val newChat = Chat(
                id = friend.id,
                name = friend.name,
                lastMessage = "Say hello 👋",
                time = "Now",
                avatarUrl = friend.avatarUrl
            )
            updatedChats.add(newChat)
            _chats.value = updatedChats

            // Cũng tạo một danh sách tin nhắn rỗng cho họ
            if (!_messages.value.containsKey(friend.id)) {
                _messages.value[friend.id] = mutableListOf()
            }
        }
    }

    fun sendImageMessage(senderId: String, receiverId: String, imageUri: String) {
        // 1. Tạo tin nhắn hình ảnh mới
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = senderId,
            receiverId = receiverId,
            type = "image", // 💡 Loại là "image"
            content = null, // Không có nội dung text
            imageUrl = imageUri, // 💡 Đường dẫn đến ảnh
            timestamp = System.currentTimeMillis().toString(),
            isRead = false,
            isSentByMe = true
        )

        // 2. Thêm tin nhắn này vào danh sách tin nhắn chi tiết
        val currentMessages = _messages.value[receiverId]?.toMutableList() ?: mutableListOf()
        currentMessages.add(0, newMessage)
        _messages.value[receiverId] = currentMessages

        // 3. CẬP NHẬT 'lastMessage' TRONG DANH SÁCH CHAT CHÍNH
        val currentChats = _chats.value.toMutableList()
        val chatIndex = currentChats.indexOfFirst { it.id == receiverId }

        if (chatIndex != -1) {
            val oldChat = currentChats[chatIndex]
            val updatedChat = oldChat.copy(
                lastMessage = "📷 Hình ảnh", //  Tin nhắn cuối là "Hình ảnh"
                time = SimpleDateFormat("HH:mm a", Locale.US).format(Date())
            )
            currentChats[chatIndex] = updatedChat
            _chats.value = currentChats // Phát ra danh sách chat đã cập nhật
        }
    }
}