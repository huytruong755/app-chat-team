package com.example.mychatapp.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Xóa import 'sampleContacts' vì Repository sẽ lo
import com.example.mychatapp.model.modelData.Contact
import kotlinx.coroutines.flow.StateFlow
// Xóa các import không cần thiết
import kotlinx.coroutines.launch

/**
 * ================================
 * ContactViewModel.kt
 * ================================
 * Trách nhiệm:
 * - Giờ đây chỉ cần 'theo dõi' (observe) và 'gọi' (call)
 * dữ liệu từ ContactRepository.
 */
class ContactViewModel : ViewModel() {

    // 1. Trỏ thẳng đến StateFlow của Repository
    val contacts: StateFlow<List<Contact>> = ContactRepository.contacts

    // Repository tự khởi tạo dữ liệu

    /**
     * 3. Hàm 'addContact' giờ sẽ gọi Repository để xử lý
     */
    fun addContact(friend: Contact) {
        viewModelScope.launch {
            // Ủy quyền việc thêm contact cho Repository
            ContactRepository.addContact(friend)
        }
    }
}