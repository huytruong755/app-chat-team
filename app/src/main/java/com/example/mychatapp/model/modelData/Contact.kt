package com.example.mychatapp.model.modelData

data class Contact(
    val id: String,
    val name: String,
    val status: String,
    val isOnline: Boolean,
    val avatarUrl: String?
)
