package com.example.firstcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TagsScreen()
        }
    }
}

@Composable
fun TagsScreen(viewModel: MainActivityViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.setSuggestedTagList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(10.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            SelectedTagsList(viewModel)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            SuggestedTagsList(viewModel)
        }
    }
}

@Composable
fun SelectedTagsList(viewModel: MainActivityViewModel) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()

            .background(Color.LightGray)
            .clip(RoundedCornerShape(10.dp))
            .padding(10.dp)

    ) {
        Text(
            text = "Selected Tags",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )

        LazyColumn {
            items(viewModel.selectedTags.size, key = { viewModel.selectedTags[it] }) { index ->
                val tag = viewModel.selectedTags[index]
                AnimatedTagItem(tag = tag, isSelected = true,isRotated=true) {
                    viewModel.removeSelectedTag(tag)
                }
            }
        }
    }
}

@Composable
fun SuggestedTagsList(viewModel: MainActivityViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.LightGray)
            .clip(RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(
            text = "Suggested Tags",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )

        LazyColumn {
            items(viewModel.suggestedTags.size, key = { viewModel.suggestedTags[it] }) { index ->
                val tag = viewModel.suggestedTags[index]
                AnimatedTagItem(tag = tag, isSelected = false,isRotated=false) {
                    viewModel.addSelectedTag(tag)
                }
            }
        }
    }
}

@Composable
fun AnimatedTagItem(tag: String, isSelected: Boolean,isRotated:Boolean, onClick: () -> Unit) {
    var startPosition by remember { mutableStateOf(IntOffset(0, 0)) }
    var targetPosition by remember { mutableStateOf(IntOffset(0, 0)) }
    val offsetAnim = remember { androidx.compose.animation.core.Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val isRotatedState =remember(tag){mutableStateOf(isRotated)}
    val rotationAngle: Float by animateFloatAsState(
        targetValue = if(isRotatedState.value) 45f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    )

    // When the composable is positioned, set the start or target position based on the list type
    LaunchedEffect(isSelected) {
        if (!isSelected) {
            offsetAnim.snapTo(0f)
        }
    }

    Row(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                // Capture the start position for suggested tags
                if (!isSelected) {
                    startPosition = coordinates.localToWindow(Offset.Zero).let { IntOffset(it.x.roundToInt(), it.y.roundToInt()) }
                } else {
                    // Capture the target position for selected tags
                    targetPosition = coordinates.localToWindow(Offset.Zero).let { IntOffset(it.x.roundToInt(), it.y.roundToInt()) }
                }
            }
            .offset { IntOffset(0, offsetAnim.value.roundToInt()) }
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable {

                // Start the animation from the start position to the target position
                coroutineScope.launch {
                    val distance = if (!isSelected) {
                        targetPosition.y - startPosition.y
                    } else {
                        startPosition.y - targetPosition.y
                    }
                    offsetAnim.animateTo(
                        targetValue = distance.toFloat(),
                        animationSpec = tween(durationMillis = 400, easing = LinearEasing)
                    )
                    isRotatedState.value=true
                    onClick()
                }
            }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = tag)
        Image(
            painter = painterResource(id = R.drawable.add),
            contentDescription = null,
            modifier = Modifier.size(24.dp).rotate(rotationAngle)
        )
    }
}











