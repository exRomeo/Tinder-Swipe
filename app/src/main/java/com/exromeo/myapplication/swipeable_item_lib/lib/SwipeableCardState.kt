package com.exromeo.myapplication.swipeable_item_lib.lib

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


enum class Direction {
    None, Left, Right,
}

@Composable
fun rememberSwipeableCardState(): SwipeableCardState {
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    return remember {
        SwipeableCardState(screenWidth)
    }
}


class SwipeableCardState(val maxWidth: Float) {

    /**
     * [offset] is an [Animatable] object to animate the swipe movement of the card.
     * */
    val offset = Animatable(offset(0f, 0f), Offset.VectorConverter)


    /**
     * [isOffScreen] is a derived state to check if the card is off the screen.
     * */
    val isOffScreen by derivedStateOf {
        offset.value.x > maxWidth || offset.value.x < -maxWidth
    }

    /**
     * The [Direction] the card was swiped at either [Direction.Left] or [Direction.Right].
     * [Direction.None] means the card was not swiped.
     */
    var swipedDirection: Direction by mutableStateOf(Direction.None)
        private set


    /**
     * [swipe] function to animate the card to swipe in a given [Direction].
     * @param direction the direction to swipe the card.
     * @param animationSpec the animation spec to be used for the swipe animation.
     * */
    suspend fun swipe(direction: Direction, animationSpec: AnimationSpec<Offset> = tween(400)) {
        val endX = maxWidth * 4f

        when (direction) {
            Direction.Left -> offset.animateTo(offset(x = -endX), animationSpec)
            Direction.Right -> offset.animateTo(offset(x = endX), animationSpec)
            Direction.None -> reset()
        }
        this.swipedDirection = direction
    }

    /**
     * [reset] resets the card to its original position.
     * */
    private suspend fun reset() {
        offset.animateTo(offset(0f, 0f), tween(400))
    }


    /**
     * [offset] helper function to create an [Offset] object while keeping the current offset values if needed.
     * */
    private fun offset(x: Float = offset.value.x, y: Float = offset.value.y): Offset {
        return Offset(x, y)
    }

    internal suspend fun drag(x: Float, y: Float) {
        offset.animateTo(offset(x, y))
    }
}
