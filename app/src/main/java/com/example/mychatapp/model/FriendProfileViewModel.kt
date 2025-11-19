package com.example.mychatapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FriendProfile(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val bio: String? = null
)

class FriendProfileViewModel : ViewModel() {
    private val api = RetrofitInstance.api
    // Thay bằng IP máy bạn
    companion object {
        // Thay bằng IP máy bạn
        private const val BASE_URL = "http://192.168.1.39:5047"
    }

    private val _uiState = MutableStateFlow<FriendProfile?>(null)
    val uiState = _uiState.asStateFlow()

    fun loadFriendProfile(phoneNumber: String) {
        viewModelScope.launch {
            try {
                // Backend không có endpoint getUserByPhone, tìm từ getAllUsers
                val response = api.getAllUsers()

                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    val userDto = users.firstOrNull { it.phoneNumber == phoneNumber }

                    if (userDto != null) {
                        val fullName = if (userDto.firstName.isNotBlank() || userDto.lastName.isNotBlank()) {
                            "${userDto.lastName} ${userDto.firstName}".trim()
                        } else {
                            phoneNumber
                        }
                        
                        _uiState.value = FriendProfile(
                            id = userDto.id.toString(),
                            name = fullName,
                            phoneNumber = userDto.phoneNumber ?: phoneNumber,
                            imageUrl = userDto.avatarUrl?.let { if (it.startsWith("http")) it else "$BASE_URL$it" }
                        )
                    } else {
                        // Không tìm thấy user
                        _uiState.value = null
                    }
                } else {
                    // Lỗi API
                    _uiState.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = null
            }
        }
    }
}
