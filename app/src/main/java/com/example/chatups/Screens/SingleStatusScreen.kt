package com.example.chatups.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatups.CommonImage
import com.example.chatups.LCViewModel

enum class State {
    INITIAL,
    ACTIVE,
    COMPLETED
}





@Composable
fun SingleStatusScreen(navController: NavController, vm: LCViewModel, userId: String) {
    Text(text = "Single Status Screen")
    val statuses = vm.status.value.filter {
        it.user.userId == userId
    }
    if (statuses.isNotEmpty()) {
        val currentStatues = remember {
            mutableStateOf(0)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            CommonImage(
                data = statuses[currentStatues.value].imageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
              statuses.forEachIndexed { index, status ->
                    CustomProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .height(7.dp)
                            .padding(1.dp),
                        state = if (index > currentStatues.value) State.INITIAL else if (index == currentStatues.value) State.ACTIVE else State.COMPLETED
                    ) {
                        if (currentStatues.value < statuses.size - 1) {
                            currentStatues.value ++
                        } else {
                            navController.popBackStack()
                        }

                    }
                }
//


            }
        }
    }

}

@Composable
fun CustomProgressIndicator(modifier: Modifier, state: State, onComplete: () -> Unit) {
    var proress = if (state == State.INITIAL) 0f else 1f
    if (state == State.ACTIVE) {
        val toggleState = remember {
            mutableStateOf(false)
        }
        LaunchedEffect(toggleState) {
            toggleState.value = true
        }

        val p: Float by animateFloatAsState(
            if (toggleState.value) 1f else 0f,
            animationSpec = tween(durationMillis = 5000),
            finishedListener = { onComplete.invoke() }
        )
        proress = p
    }
        LinearProgressIndicator(modifier = modifier, color = Color.Red, progress = proress)




}
