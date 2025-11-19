package com.example.mychatapp.network

import com.example.mychatapp.network.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("Auth/login")
    suspend fun login(@Body loginDto: LoginRequestDto): Response<LoginResponseDto>

    @PUT("Auth/update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body dto: UpdateProfileDto
    ): Response<Any>

    // --- USER ---
    @GET("User/list")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @GET("User/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>

    // --- CONTACTS ---
    @GET("Contact")
    suspend fun getMyContacts(
        @Header("Authorization") token: String
    ): Response<ContactResponseWrapper>

    // --- CHAT ---
    @GET("Chat")
    suspend fun getChatConversations(
        @Header("Authorization") token: String
    ): Response<List<ChatResponseDto>>

    // --- MESSAGES ---
    @GET("Message/{chatId}")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int
    ): Response<List<MessageResponseDto>>

    @Multipart
    @POST("Message/send")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Part("ChatId") chatId: RequestBody?,
        @Part("SenderId") senderId: RequestBody,
        @Part("ReceiverId") receiverId: RequestBody,
        @Part("FileType") fileType: RequestBody,
        @Part("Content") content: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<SendMessageResponseDto>
}
