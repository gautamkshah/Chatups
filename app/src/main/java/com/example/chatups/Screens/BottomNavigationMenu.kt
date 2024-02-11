package com.example.chatups.Screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatups.DestinationScreen
import com.example.chatups.R
import com.example.chatups.navigateTo

enum class BottomNavigationItem(val icon: Int,val navDestination: DestinationScreen) {
    CHATLIST(R.drawable.message, DestinationScreen.ChatList),
    STATUS(R.drawable.status, DestinationScreen.StatusList),
    PROFILE(R.drawable.profile, DestinationScreen.Profile),
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    Row(
        modifier=Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 8.dp)

            .background(Color.White)
    ){
        for(item in BottomNavigationItem.entries){
            Image(painter= painterResource(id = item.icon), contentDescription = null,
               modifier= Modifier
                   .size(40.dp)
                    .padding(4.dp)
                   .weight(1f)
                    .clickable {
                        navigateTo(navController = navController,route = item.navDestination.route)
                    },
                colorFilter = if(selectedItem==item)
                    ColorFilter.tint(color= Color.Black)
                else
                     ColorFilter.tint(color= Color.Gray
                )
            )

        }
    }

}