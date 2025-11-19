package com.example.mychatapp.model.viewModel

import android.util.Log
import com.example.mychatapp.model.modelData.Contact
import com.example.mychatapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ContactRepository {

    private val api = RetrofitInstance.api

    // Thay bằng IP máy của bạn (như file ChatRepository)
    private const val BASE_URL = "http://192.168.1.39:5047"

    // ID của user hiện tại (set khi Login)
    var currentUserId: Int = 1
    private var authToken: String = ""

    fun setAuthToken(token: String) {
        authToken = token
    }

    // 1. Danh sách bạn bè (Đã kết bạn)
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    // 2. Danh sách tất cả user (Để tìm kiếm kết bạn)
    private val _allUsers = MutableStateFlow<List<Contact>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    // === API: Lấy danh bạ ===
    fun fetchContacts() {
        if (authToken.isEmpty()) {
            Log.e("ContactRepo", "Auth token is empty, cannot fetch contacts")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getMyContacts("Bearer $authToken")
                if (response.isSuccessful && response.body() != null) {
                    val wrapper = response.body()!!

                    // Map từ DTO sang Model UI (Contact.kt)
                    val mappedList = wrapper.contacts.map { item ->
                        val info = item.contactInfo
                        Contact(
                            id = info.id,
                            name = info.fullName, // ContactUserInfoDto có fullName, không có firstName/lastName riêng
                            avatarUrl = info.avatarUrl?.let { if (it.startsWith("http")) it else "$BASE_URL$it" },
                            isOnline = info.isOnline ?: false,
                            status = if (info.isOnline == true) "Online" else "Offline"
                        )
                    }
                    _contacts.value = mappedList
                } else {
                    Log.e("ContactRepo", "Failed to fetch contacts: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ContactRepo", "Lỗi lấy danh bạ: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // === API: Lấy tất cả user để tìm kiếm ===
    fun fetchAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getAllUsers()
                if (response.isSuccessful && response.body() != null) {
                    val dtoList = response.body()!!
                    val mappedList = dtoList.map { u ->
                        val fullName = if (u.firstName.isNotBlank() || u.lastName.isNotBlank()) {
                            "${u.lastName} ${u.firstName}".trim()
                        } else {
                            u.phoneNumber ?: "Unknown"
                        }
                        Contact(
                            id = u.id,
                            name = fullName,
                            avatarUrl = u.avatarUrl?.let { if (it.startsWith("http")) it else "$BASE_URL$it" },
                            isOnline = u.isOnline ?: false,
                            status = if (u.isOnline == true) "Online" else "Offline"
                        )
                    }
                    // Lọc bỏ bản thân mình ra khỏi danh sách tìm kiếm
                    _allUsers.value = mappedList.filter { it.id != currentUserId }
                } else {
                    Log.e("ContactRepo", "Failed to fetch all users: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ContactRepo", "Lỗi lấy list user: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // === API: Thêm bạn ===
    // LƯU Ý: Backend không có endpoint addFriend, có thể cần implement sau
    fun addFriend(friendId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // TODO: Implement endpoint addFriend trong backend
                // Hiện tại backend không có endpoint này, nên chỉ log
                Log.w("ContactRepo", "addFriend endpoint not implemented in backend yet")
                withContext(Dispatchers.Main) {
                    onError()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onError()
                }
            }
        }
    }

    // Hàm clear dữ liệu khi logout
    fun clear() {
        _contacts.value = emptyList()
        _allUsers.value = emptyList()
    }

    fun addSelfToLocal(user: Contact) {
        val currentList = _contacts.value.toMutableList()
        // Xóa user cũ nếu trùng ID (để cập nhật mới)
        currentList.removeIf { it.id == user.id }
        // Thêm user mới vào đầu danh sách (hoặc cuối tùy bạn)
        currentList.add(0, user)
        _contacts.value = currentList
    }
}