package com.example.chatups

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatups.Screens.ChatListScreen
import com.example.chatups.Screens.LoginScreen
import com.example.chatups.Screens.ProfileScreen
import com.example.chatups.Screens.SignUpScreen
import com.example.chatups.Screens.SingleChatScreen
import com.example.chatups.Screens.SingleStatusScreen
import com.example.chatups.Screens.StatusScreen
import com.example.chatups.ui.theme.ChatupsTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(var route: String) {
    object SignUp : DestinationScreen("signup")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatList")
    object SingleChat : DestinationScreen("singleChat/{chatId}") {
        fun createRoute(id: String) = "singlechat/$id"
    }
    object StatusList : DestinationScreen("StatusList")
    object SingleStatus : DestinationScreen("singleStatus/{userId}") {
        fun createRoute(userId: String) = "singleStatus/$userId"
    }

}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatupsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }
        }
    }
    @Composable
    fun ChatAppNavigation(){
//        var vm=hiltViewModel<LCViewModel>()
        val navcontroller= rememberNavController()
        var vm=hiltViewModel<LCViewModel>()
        NavHost(navController = navcontroller, startDestination = DestinationScreen.SignUp.route){

            composable(DestinationScreen.SignUp.route){
                SignUpScreen(navcontroller,vm)
            }
            composable(DestinationScreen.Login.route){
                LoginScreen(vm,navcontroller)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(navcontroller,vm)
            }
            composable(DestinationScreen.SingleChat.route){
                val chatid=it.arguments?.getString("chatId")
                chatid?.let{
                    SingleChatScreen(navcontroller,vm,chatid)
                }
            }
            composable(DestinationScreen.StatusList.route){
                StatusScreen(navController = navcontroller, vm = vm)
            }
            composable(DestinationScreen.Profile.route) {
                ProfileScreen(navcontroller,vm)
            }
            composable(DestinationScreen.SingleStatus.route){
                val userId=it.arguments?.getString("userId")
                userId?.let{
                    SingleStatusScreen(navcontroller,vm,it)
                }

            }

        }


    }


}
