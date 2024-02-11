package com.example.chatups.Screens


import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatups.CommonDivider
import com.example.chatups.CommonImage
import com.example.chatups.CommonProgressBar
import com.example.chatups.DestinationScreen
import com.example.chatups.LCViewModel
import com.example.chatups.navigateTo

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController, vm: LCViewModel) {
    val inProgress = vm.inProgress.value


    if (inProgress) {
        CommonProgressBar()
    } else {
        val userData = vm.userData.value

        var name by rememberSaveable {
            mutableStateOf(userData?.name ?: "")
        }
        var number by rememberSaveable {
            mutableStateOf(userData?.number ?: "")
        }
        Column {
            Scaffold(
                content = {
                    ProfileContent(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        vm = vm,
                        name = name,
                        number = number,
                        onNameChange = {
                            name = it
                        },
                        onNumberChange = {
                            number = it
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                        OnSave = {
                            vm.createOrUpdateProfile(name = name, number = number)

                        },
                        onLogout = {
                            vm.logout()
                            navigateTo(
                                navController = navController,
                                route = DestinationScreen.Login.route
                            )
                        }
                    )

                },
                bottomBar = {
                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.PROFILE,
                        navController = navController
                    )

                }
            )
//            ProfileContent(
//                modifier = Modifier
//                    .weight(1f)
//                    .verticalScroll(rememberScrollState())
//                    .padding(8.dp),
//                vm = vm,
//                name = name,
//                number = number,
//                onNameChange = {
//                               name=it
//                },
//                onNumberChange = {
//                                 number=it
//                },
//                onBack = {
//                    navController.popBackStack()
//                },
//                OnSave = {
//                    vm.createOrUpdateProfile(name=name,number= number)
//
//                },
//                onLogout = {
//                    vm.logout()
//                    navigateTo(navController=navController,route=DestinationScreen.Login.route)
//                }
//            )
//            BottomNavigationMenu(
//                selectedItem = BottomNavigationItem.PROFILE,
//                navController = navController
//            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier,
    vm: LCViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onBack: () -> Unit,
    OnSave: () -> Unit,
    onLogout: () -> Unit,
) {


    val imageUrl = vm.userData.value?.imageUrl

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back",  Modifier.clickable { onBack.invoke() })
            Text(text = "Save",  Modifier.clickable { OnSave.invoke() })
        }
        CommonDivider()
        ProfileImage(imageUrl, vm)

        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = "Name", modifier = Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.Black,
                    containerColor = Color.Transparent
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = "Number", Modifier.width(100.dp))
            TextField(
                value = number,
                onValueChange = onNumberChange,
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.Black,
                    containerColor = Color.Transparent,
                )
            )
        }
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Logout", modifier = Modifier.clickable { onLogout.invoke() })

        }


    }

}


@Composable
fun ProfileImage(imageurl: String?, vm: LCViewModel) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }

    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min))
    {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageurl)
            }
            Text(text = "Change Profile Picture")

        }
        val isLoading = vm.inProgress.value
        if (isLoading) {
            CommonProgressBar()
        }


    }

}