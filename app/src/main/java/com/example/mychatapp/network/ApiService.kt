package com.example.mychatapp.network

import com.example.mychatapp.model.modelData.Chat
import com.example.mychatapp.model.modelData.ChatMessage
import com.example.mychatapp.model.modelData.Contact
import com.example.mychatapp.network.dto.LoginRequestDto
import com.example.mychatapp.network.dto.LoginResponseDto
import com.example.mychatapp.network.dto.RegisterRequestDto

import retrofit2.Response
import retrofit2.http.Body // 💡 THÊM IMPORT
import retrofit2.http.GET
import retrofit2.http.POST // 💡 THÊM IMPORT
import retrofit2.http.Path

/**
 * ApiService (Thực đơn)
 * Định nghĩa TẤT CẢ các lệnh gọi API đến máy chủ C#.
 * Retrofit sẽ "thực hiện" các hàm này.
 */
interface ApiService {

    // --- Ví dụ cho Contact ---

    @POST("Auth/login")
    suspend fun login( // 1. Đổi tên hàm
        @Body loginDto: LoginRequestDto // 2. Đổi tham số
    ): Response<LoginResponseDto>
    /**
     * Lấy danh sách bạn bè
     * Tương đương: GET /api/contacts
     */
    @GET("api/contacts")
    suspend fun getContacts(): List<Contact>

    // --- Ví dụ cho Chat ---

    /**
     * Lấy danh sách các cuộc hội thoại
     * Tương đương: GET /api/chat
     */
    @GET("api/chat")
    suspend fun getChatConversations(): List<Chat>

    /**
     * Lấy lịch sử tin nhắn cho một người bạn
     * Tương đương: GET /api/chat/123/messages
     */
    @GET("api/chat/{friendId}/messages")
    suspend fun getChatHistory(@Path("friendId") friendId: String): List<ChatMessage>

    // --- Ví dụ cho Auth (Sẽ thêm sau) ---
    // @POST("api/auth/send-otp")
    // suspend fun sendOtp(...)

    // @POST("api/chat/send/text")
    // suspend fun sendTextMessage(...)
    @POST("Auth/register")
    suspend fun register(@Body registerDto: RegisterRequestDto): Response<LoginResponseDto>

}

