package com.example.mychatapp.data

import com.example.mychatapp.model.Contact

// --- DỮ LIỆU GIẢ CHO TEST UI---
val sampleContacts = listOf(
    Contact("1", "Athalia Putri", "Last seen yesterday", false, "https://i.pravatar.cc/150?img=1"),
    Contact("2", "Erlan Sadewa", "Online", true, "https://i.pravatar.cc/150?img=2"),
    Contact("3", "Midala Huera", "Last seen 3 hours ago", false, "https://i.pravatar.cc/150?img=3"),
    Contact("4", "Nafisa Gitari", "Online", true, "https://i.pravatar.cc/150?img=4"),
    Contact("5", "Raki Devon", "Online", true, null), // Sẽ hiển thị chữ "RD"
    Contact("6", "Salsabila Akira", "Last seen 30 minutes ago", false, null) // Sẽ hiển thị chữ "SA"
)


