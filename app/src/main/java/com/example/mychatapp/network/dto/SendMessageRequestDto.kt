package com.example.mychatapp.network.dto

/**
 * SendMessageRequestDto - Không còn dùng nữa
 * Thay vào đó, sử dụng MultipartBody trong ApiService.sendMessage()
 * để gửi file và form data khớp với backend [FromForm] SendMessageDto
 * 
 * Backend nhận: ChatId?, SenderId, ReceiverId, FileType, Content, File (IFormFile)
 */