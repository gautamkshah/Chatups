package com.example.chatups.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatups.CheckSignIn
import com.example.chatups.CommonProgressBar
import com.example.chatups.DestinationScreen
import com.example.chatups.LCViewModel
import com.example.chatups.R
import com.example.chatups.navigateTo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(NavController: NavController, vm: LCViewModel )
{
    CheckSignIn(vm,NavController)
    Box( modifier=Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var nameState = remember {
                mutableStateOf(TextFieldValue())
            }
            var numberState = remember {
                mutableStateOf(TextFieldValue())
            }
            var emailState = remember {
                mutableStateOf(TextFieldValue())
            }
            var passwordState = remember {
                mutableStateOf(TextFieldValue())
            }
            val focus = LocalFocusManager.current

            Image(
                painter = painterResource(id = R.drawable.chatlogo),
                contentDescription = null, modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Sign Up",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)

            )
            OutlinedTextField(
                value = nameState.value,
                onValueChange = {
                    nameState.value = it
                },
                label = {
                    Text(text = "Name")
                },
                modifier = Modifier
                    .padding(8.dp)

            )
            OutlinedTextField(
                value = numberState.value,
                onValueChange = {
                    numberState.value = it
                },
                label = {
                    Text(text = "Number")
                },
                modifier = Modifier
                    .padding(8.dp)

            )
            OutlinedTextField(
                value = emailState.value,
                onValueChange = {
                    emailState.value = it
                },
                label = {
                    Text(text = "Email")
                },
                modifier = Modifier
                    .padding(8.dp)

            )
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                },
                label = {
                    Text(text = "Password")
                },
                modifier = Modifier
                    .padding(8.dp)

            )

            Button(onClick = {
                vm.signUp(
                   name= nameState.value.text,
                   number= numberState.value.text,
                  email=  emailState.value.text,
                    password=passwordState.value.text
                )

            }, modifier = Modifier
                .padding(8.dp))
            {
                //Text(text = nameState.value.text)
                Text(text = "Sign Up")

            }

            Text(text = "Already have an account->",
                color = androidx.compose.ui.graphics.Color.Green,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(NavController, DestinationScreen.Login.route)
                    }
            )

        }

    }
    if(vm.inProgress.value){
        CommonProgressBar()
    }

}


