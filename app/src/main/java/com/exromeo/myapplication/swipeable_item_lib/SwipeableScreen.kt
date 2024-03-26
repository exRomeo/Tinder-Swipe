package com.exromeo.myapplication.swipeable_item_lib

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.exromeo.myapplication.SwipesViewModel
import com.exromeo.myapplication.swipeable_item_lib.lib.Direction
import com.exromeo.myapplication.swipeable_item_lib.lib.SwipeableCardState
import com.exromeo.myapplication.swipeable_item_lib.lib.rememberSwipeableCardState
import com.exromeo.myapplication.swipeable_item_lib.lib.swipableCard
import kotlinx.coroutines.launch

@Composable
fun SwipeableScreen(viewModel: SwipesViewModel, isSwipingEnabled: State<Boolean>) {

    Box(
        Modifier
            .padding(24.dp)
            .fillMaxSize()
            .aspectRatio(1f)
    ) {
        viewModel.list.fastForEachIndexed { index, matchProfile ->
            val state = rememberSwipeableCardState()
            if (state.swipedDirection == Direction.None) {

                ProfileCard(
                    modifier = Modifier
                        .fillMaxSize(),
                    matchProfile = matchProfile,
                    isSwipingEnabled = isSwipingEnabled
                )
            }
        }
    }


}


@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .size(56.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        onClick = onClick
    ) {
        Icon(
            icon, null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ProfileCard(
    modifier: Modifier,
    matchProfile: MatchProfileUiModel,
    isSwipingEnabled: State<Boolean>,
) {
    val state: SwipeableCardState = rememberSwipeableCardState()
    Log.i("TAG", "ProfileCard: ${state.isOffScreen()}")
    if (state.isOffScreen()/*swipedDirection != Direction.None*/) return
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Box(modifier
        .swipableCard(
            enableUserSwiping = isSwipingEnabled,
            state = state,
            onSwiped = {
                when (it) {
                    Direction.Left -> {
                        matchProfile.onSwipeLeft()
                    }

                    Direction.Right -> {
                        matchProfile.onSwipeRight()
                    }

                    Direction.None -> {}
                }

                context.showToast(stringFrom(state.swipedDirection))
            }
        )
        .clip(MaterialTheme.shapes.medium)) {
        Image(
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(matchProfile.drawableResId),
            contentDescription = null
        )
        Scrim(Modifier.align(Alignment.BottomCenter))
        Column(Modifier.align(Alignment.BottomStart)) {
            Text(
                text = matchProfile.name,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(10.dp)
            )
        }


        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircleButton(
                onClick = {
                    scope.launch {
                        state.swipe(Direction.Left)
                        matchProfile.onSwipeLeft()
                    }
                },
                icon = Icons.Rounded.Close
            )
            CircleButton(
                onClick = {
                    scope.launch {
                        state.swipe(Direction.Right)
                        matchProfile.onSwipeRight()
                    }
                },
                icon = Icons.Rounded.Favorite
            )
        }

    }
}

@Composable
private fun Hint(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}


private fun stringFrom(direction: Direction): String {
    return when (direction) {
        Direction.Left -> "you swiped Left <<-"
        Direction.Right -> "you swiped Right ->>"
        Direction.None -> "You canceled the swipe =="
    }
}


@Composable
fun Scrim(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .height(180.dp)
            .fillMaxWidth()
    )
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
