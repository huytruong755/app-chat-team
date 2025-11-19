package com.example.mychatapp.network

import android.util.Log
import com.example.mychatapp.network.dto.MessageResponseDto
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object SignalRService {

    private const val HUB_URL = "http://192.168.1.39:5047/chatHub"

    private var hubConnection: HubConnection? = null

    private val _incomingMessages = MutableSharedFlow<MessageResponseDto>()
    val incomingMessages = _incomingMessages.asSharedFlow()

    fun startConnection(token: String) {
        try {
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) return

            hubConnection = HubConnectionBuilder.create(HUB_URL)
                .withAccessTokenProvider(Single.defer { Single.just(token) })
                .build()

            // Nhận message realtime
            hubConnection?.on("ReceiveMessage", { msgDto ->
                CoroutineScope(Dispatchers.IO).launch {
                    _incomingMessages.emit(msgDto)
                    Log.d("SignalR", "Received message: ${msgDto.content ?: "[File]"}")
                }
            }, MessageResponseDto::class.java)

            hubConnection?.onClosed {
                Log.d("SignalR", "Connection closed")
            }

            hubConnection?.start()?.blockingAwait()
            Log.d("SignalR", "Connected. ID: ${hubConnection?.connectionId}")

        } catch (e: Exception) {
            Log.e("SignalR", "Connection error: ${e.message}")
        }
    }

    fun stopConnection() {
        try {
            hubConnection?.stop()
        } catch (e: Exception) {
            Log.e("SignalR", "Error stopping connection: ${e.message}")
        } finally {
            hubConnection = null
        }
    }

    fun joinChatGroup(chatId: String) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            try {
                hubConnection?.invoke("JoinGroup", chatId)
                Log.d("SignalR", "Joined group: $chatId")
            } catch (e: Exception) {
                Log.e("SignalR", "JoinGroup error: ${e.message}")
            }
        } else {
            Log.e("SignalR", "Not connected — cannot join group")
        }
    }
}

