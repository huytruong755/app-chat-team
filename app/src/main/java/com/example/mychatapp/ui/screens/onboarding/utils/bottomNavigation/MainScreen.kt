package com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mychatapp.ui.screens.onboarding.AuthenticationEnterCode
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.addFriendScreen.OnboardingAddFriend
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.addFriendScreen.OnboardingContacts
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.chats.OnboardingChatDetail
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.chats.OnboardingChats
import com.example.mychatapp.ui.screens.onboarding.utils.bottomNavigation.mores.OnboardingSelfProfile

@Composable
fun MainScreen(navController: NavController) {
    // ⚠️ Tạo NavController riêng cho bottom bar
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "addFriend" && currentRoute?.startsWith("chat_detail") != true) {
                BottomNavigationBar(bottomNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "contacts",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("contacts") {
                OnboardingContacts(bottomNavController)
            }
            composable("messages") {
                OnboardingChats(bottomNavController)
            }
            composable("profile") {
                OnboardingSelfProfile(bottomNavController)
            }
            composable("addFriend") {
                OnboardingAddFriend(bottomNavController)
            }
            composable (
                route = "NumberCode/{friendPhoneNumber}",
                arguments = listOf(navArgument("friendPhoneNumber") { type = NavType.StringType })
            ){ backStackEntry ->
                // lấy argument "friendPhoneNumber" và gán vào biến tên "phoneNumber"
                val phoneNumber = backStackEntry.arguments?.getString("friendPhoneNumber") ?: ""

                // truyền biến "phoneNumber" (ở trên) vào đây,
                // không phải truyền "friendPhoneNumber" (là tên của key)
                AuthenticationEnterCode(bottomNavController, phoneNumber)
            }
            composable("chats") { OnboardingChats(navController) }
            composable(
                route = "chat_detail/{friendId}/{friendName}",
                arguments = listOf(
                    navArgument("friendId") { type = NavType.StringType },
                    navArgument("friendName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                // 🧩 Lấy dữ liệu truyền qua NavController
                val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                val friendName = backStackEntry.arguments?.getString("friendName") ?: ""

                /**
                 * ============================================
                 * Truyền dữ liệu cho OnboardingChatDetail
                 * ============================================
                 * senderId: ID của người dùng hiện tại (có thể lấy từ session/token)
                 * receiverId: ID của bạn bè đang nhắn
                 * receiverName: tên hiển thị
                 *
                 * Khi backend thật hoạt động:
                 *  - senderId = userSession.userId (hoặc lấy từ ViewModel)
                 *  - receiverId = friendId (được truyền từ danh sách bạn bè)
                 *
                 * ChatViewModel / OnboardingChatDetail sẽ dùng receiverId để gọi:
                 *      GET /api/messages/{conversationId hoặc friendId}
                 * để load dữ liệu chat thật
                 */
                OnboardingChatDetail(
                    navController = bottomNavController,
                    senderId = "1", // TODO: sau này thay bằng userId từ session login thật
                    receiverId = friendId,
                    receiverName = friendName
                )
            }
        }
    }
}