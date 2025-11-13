package com.example.mychatapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FriendProfile(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val bio: String
)

class FriendProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<FriendProfile?>(null)
    val uiState: StateFlow<FriendProfile?> = _uiState

    fun loadFriendProfile(friendId: String) {
        viewModelScope.launch {
            // TODO: gọi repo hoặc API để lấy thông tin bạn bè
            _uiState.value = FriendProfile(
                id = friendId,
                name = "Nguyễn Văn B",
                phoneNumber = "+84987654321",
                imageUrl = null,
                bio = "Hello, I am B!"
            )
        }
    }
}
