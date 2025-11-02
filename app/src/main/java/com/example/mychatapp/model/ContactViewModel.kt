package com.example.mychatapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.data.sampleContacts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModel : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            // ================================================
            // 👉 TODO: NƠI LÀM VIỆC CỦA BACKEND C#
            // 1. Khởi tạo ApiService (Retrofit/Ktor)
            // 2. val realContacts = apiService.getContacts()
            // 3. _contacts.value = realContacts
            // ================================================

            // Tạm thời dùng data giả
            //thay chỗ này để lấy dữ liệu từ API
            _contacts.value = sampleContacts
        }
    }
}