package com.example.mychatapp.model

data class Contact(
    val name: String,
    val status: String,
    val isOnline: Boolean,
    val avatarRes: Int? = null
)