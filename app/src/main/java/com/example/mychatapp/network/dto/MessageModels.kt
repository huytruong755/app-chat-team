package com.example.mychatapp.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Response từ Message/{ChatId}
 * Trả về danh sách MessageDto trực tiếp (không có wrapper)
 * Mỗi message có: Id, SenderId, SenderName, Content, FileUrl, FileType, SentTime
 */