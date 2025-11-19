package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mychatapp.model.modelData.Chat
import com.example.mychatapp.model.viewModel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingChats(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val chats by viewModel.chats.collectAsState()

    // Load danh sách chat từ backend
    LaunchedEffect(Unit) {
        viewModel.refreshChats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                val filteredChats = chats.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                items(filteredChats) { chat ->
                    ChatItem(
                        chat = chat,
                        onClick = {
                            // Điều hướng sang OnboardingChatDetail
                            // chat.id là chatId từ server, chat.partnerId là friendId
                            val friendId = chat.partnerId?.toString() ?: chat.id.toString()
                            navController.navigate("chat_detail/${chat.id}/${friendId}/${chat.name}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )
        },
        placeholder = {
            Text(
                text = "Search chats",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF6F6F6),
            unfocusedContainerColor = Color(0xFFF6F6F6),
            disabledContainerColor = Color(0xFFF6F6F6),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Gray,
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.Gray
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
    )
}

@Composable
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp)) {
            if (chat.avatarUrl != null) {
                AsyncImage(
                    model = chat.avatarUrl,
                    contentDescription = chat.name,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E9FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chat.name.split(" ").map { it.first() }.take(2).joinToString(""),
                        color = Color(0xFF1E40AF),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = chat.time,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
