package com.example.chatups.Screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chatups.CommonDivider
import com.example.chatups.CommonImage
import com.example.chatups.LCViewModel
import com.example.chatups.data.Message

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SingleChatScreen(navcontroller: NavHostController, vm: LCViewModel,chatId: String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }

    val onSendReply={
        vm.onSendReply(chatId,reply)
        reply=""
    }
    val chatMessage=vm.chatMessages
    val myUser=vm.userData.value
    val currentChat=vm.chats.value.first{it.chatId==chatId}
    val chatUser=if(myUser?.userId==currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit){
        vm.populateMessages(chatId)
    }
    BackHandler {
        vm.depopulateMessages()
    }

    Column {
        Scaffold (
            topBar = {
                ChatHeader(name = chatUser.name?:"",imageUrl = chatUser.imageUrl?:"",onBackClicked = {
                    navcontroller.popBackStack()
                    vm.depopulateMessages()
                })
            },
            content = {

                MessageBox(modifier = Modifier.weight(1f).fillMaxWidth(),chatMessages = chatMessage.value,currentUserId = myUser?.userId?:"")
            },
            bottomBar = {
                ReplyBox(reply = reply, onReplyChange = { reply=it }, onSendReply = onSendReply)
            }
        )

//        ChatHeader(name = chatUser.name?:"",imageUrl = chatUser.imageUrl?:""){
//            navcontroller.popBackStack()
//            vm.depopulateMessages()
//         }
//        MessageBox(modifier = Modifier.weight(1f),chatMessages = chatMessage.value,currentUserId = myUser?.userId?:"")
//        bottombar={ReplyBox(reply = reply, onReplyChange = { reply=it }, onSendReply = onSendReply)}

    }
}

@Composable
fun MessageBox(
    modifier: Modifier,
    chatMessages:List<Message>,
    currentUserId:String
){
    LazyColumn(modifier=Modifier){
        items(chatMessages){
            msg->
            val alignment=if(msg.sendBy==currentUserId) {
                Alignment.End
            }else{
                Alignment.Start
            }
            val color=if(msg.sendBy==currentUserId) {
                androidx.compose.ui.graphics.Color(0xFFC0C0C0)
            }else{
                androidx.compose.ui.graphics.Color(0xFFC0C0C0)
            }
            Column (modifier= Modifier
                .fillMaxWidth()
                .padding(8.dp),
                horizontalAlignment = alignment
            ){
                Text(text = msg.message?:"",modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(2.dp ,shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .background(color),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold)
            }

        }
    }


}


@Composable
fun ChatHeader(
    name:String,imageUrl:String,
    onBackClicked:()->Unit,
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(Icons.Rounded.ArrowBack, contentDescription =null,modifier=
        Modifier
            .clickable { onBackClicked.invoke() }
            .padding(8.dp))
        CommonImage(data= imageUrl,modifier = Modifier
            .padding(8.dp)
            .size(50.dp)
            .clip(CircleShape))
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start= 4.dp).fillMaxWidth())
    }

}

@Preview
@Composable
fun ReplyBox(
    reply:String,
    onReplyChange:(String)->Unit,
    onSendReply:()->Unit
){

    Column(
        modifier=Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End

    ) {
        CommonDivider()
        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            TextField(value=reply, onValueChange = onReplyChange, maxLines = 3)
            Button(onClick = onSendReply) {
                Text(text = "Send->")

            }
        }

    }


}