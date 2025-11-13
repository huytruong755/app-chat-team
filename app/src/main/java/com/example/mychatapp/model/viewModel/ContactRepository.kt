package com.example.mychatapp.model.viewModel // Hoặc package của bạn

import com.example.mychatapp.data.sampleContacts
import com.example.mychatapp.model.modelData.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ================================
 * ContactRepository.kt (Singleton)
 * ================================
 * Đây là "Nguồn sự thật chung" cho Danh bạ.
 * Nó là một 'object' (singleton) để ContactViewModel
 * (ở màn hình Contacts) và ContactViewModel (ở màn hình
 * FriendInformationScreen) có thể truy cập và
 * chia sẻ cùng một dữ liệu.
 */
object ContactRepository {

    // 1. Giữ danh sách contact, khởi tạo bằng dữ liệu mẫu
    private val _contacts = MutableStateFlow(sampleContacts)
    val contacts = _contacts.asStateFlow()

    /**
     * 2. Logic thêm contact
     * (Đã được di chuyển từ ContactViewModel)
     */
    fun addContact(friend: Contact) {
        val updatedList = _contacts.value.toMutableList()

        // Nếu chưa có trong danh bạ thì thêm
        if (updatedList.none { it.id == friend.id }) {
            updatedList.add(friend)
            // 3. Phát ra giá trị mới
            _contacts.value = updatedList
        }
    }
}