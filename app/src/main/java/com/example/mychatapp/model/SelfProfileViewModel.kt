package com.example.mychatapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.data.sampleSelfProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * SelfProfileViewModel
 * - Quản lý dữ liệu hồ sơ của chính người dùng (mục More)
 * - uiState hiện tại chứa SelfProfile (id, name, phoneNumber, imageUrl, bio)
 *
 * Ghi chú:
 * - Hiện dùng sampleSelfProfile để demo
 * - Sau này: tích hợp Repository -> load từ local DB (DataStore/Room) trước,
 *   rồi gọi API GET /user/self để cập nhật nếu có mạng.
 */

data class SelfProfile(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val imageUrl: String?,
    val bio: String
)

class SelfProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(sampleSelfProfile)
    val uiState: StateFlow<SelfProfile> = _uiState

    /**
     * Load profile:
     * - Bước 1: load từ local cache (Room/DataStore)
     * - Bước 2: nếu có mạng -> call API GET /user/self -> cập nhật local và _uiState
     */
    fun loadSelfProfile() {
        viewModelScope.launch {
            // TODO: replace bằng repo.getSelf() để lấy dữ liệu thật
            _uiState.value = sampleSelfProfile
        }
    }

    /**
     * Cập nhật profile:
     * - update local cache
     * - gọi API PUT /user/self để đồng bộ lên server
     */
    fun updateSelfProfile(name: String, bio: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(name = name, bio = bio)
            // TODO: repo.updateSelf(...) -> lưu Room/DataStore và gọi API
        }
    }
}
