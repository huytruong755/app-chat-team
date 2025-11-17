package com.example.mychatapp.ui.screens.onboarding.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey // Thêm import
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

class SessionManager(private val context: Context) {
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        // 💡 THÊM KEY MỚI
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    // Luồng (Flow) cho trạng thái đăng nhập
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
        it[IS_LOGGED_IN] ?: false
    }

    // 💡 THÊM LUỒNG CHO ID VÀ TÊN
    val userId: Flow<String?> = context.dataStore.data.map {
        it[USER_ID]
    }

    val userName: Flow<String?> = context.dataStore.data.map {
        it[USER_NAME]
    }

    /**
     * 💡 THÊM HÀM LƯU TOÀN BỘ PHIÊN ĐĂNG NHẬP
     */
    suspend fun saveUserSession(id: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_ID] = id
            prefs[USER_NAME] = name
        }
    }

    /**
     * 💡 THÊM HÀM ĐĂNG XUẤT
     */
    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs.clear() // Xóa tất cả
        }
    }

    // Hàm cũ, chúng ta sẽ không dùng trực tiếp nữa nhưng có thể giữ lại
    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = value
        }
    }
}