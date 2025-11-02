package com.example.mychatapp.data

import com.example.mychatapp.model.Chat

// 💾 DỮ LIỆU MẪU – Tạm thời hiển thị cho UI.
// Khi backend hoàn thành, ViewModel sẽ thay thế bằng dữ liệu thật qua API.
val sampleChats = listOf(
    Chat("1", "Athalia Putri", "Hey, how are you?", "09:45 AM", "https://i.pravatar.cc/150?img=1", ),
    Chat("2", "Erlan Sadewa", "Let's meet tomorrow!", "Yesterday","https://i.pravatar.cc/150?img=2" ),
    Chat("3", "Midala Huera", "Typing...", "Now", "https://i.pravatar.cc/150?img=3"),
    Chat("4", "Raki Devon", "Good night!", "Mon", null),
    Chat("5", "Nafisa Gitari", "Thank you ❤️", "Sun", "https://i.pravatar.cc/150?img=4")
)
