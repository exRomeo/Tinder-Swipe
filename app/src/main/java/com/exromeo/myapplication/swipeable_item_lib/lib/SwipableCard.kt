package com.exromeo.myapplication.swipeable_item_lib.lib

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.exromeo.myapplication.TAG
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Enables Tinder like swiping gestures.
 *
 * @param state The current state of the swipeable card. Use [rememberSwipeableCardState] to create.
 * @param onSwiped will be called once a swipe gesture is completed. The given [Direction] will indicate which side the gesture was performed on.
 * @param onSwipeCancel will be called when the gesture is stopped before reaching the minimum threshold to be treated as a full swipe
 * @param blockedDirections the directions which will not trigger a swipe. By default only horizontal swipes are allowed.
 */
fun Modifier.swipableCard(
    enableUserSwiping:State<Boolean> = mutableStateOf(true),
    state: SwipeableCardState,
    onSwiped: (Direction) -> Unit,
) = pointerInput(Unit) {
    if (enableUserSwiping.value)
        coroutineScope {
            detectDragGestures(
                onDragCancel = {
                    launch {
                        state.swipe(Direction.None)
                        onSwiped(Direction.None)
                    }
                },
                onDrag = { change, dragAmount ->
                    launch {
                        val original = state.offset.targetValue
                        val summed = original + dragAmount
                        Log.i(TAG, "swipableCard: ${summed.x}")
                        val newValue = Offset(
                            //coerceIn here is used to control how far the item is swiped left or right
                            x = summed.x/*.coerceIn(-state.maxWidth, state.maxWidth)*/,
                            y = summed.y
                        )
                        if (change.positionChange() != Offset.Zero) change.consume()
                        state.drag(newValue.x, newValue.y)
                    }
                },
                onDragEnd = {
                    launch {

                        if (hasNotTravelledEnough(state, state.offset.targetValue)) {
                            state.swipe(Direction.None)
                            onSwiped(Direction.None)
                        } else {
                            if (state.offset.targetValue.x > 0) {
                                state.swipe(Direction.Right)
                                onSwiped(Direction.Right)
                            } else {
                                state.swipe(Direction.Left)
                                onSwiped(Direction.Left)
                            }
                        }

                    }
                }
            )
        }
}.graphicsLayer {
    translationX = state.offset.value.x
    //dictates the rotation of the item
    rotationZ = (state.offset.value.x / 70).coerceIn(-90f, 90f)
}


private fun hasNotTravelledEnough(
    state: SwipeableCardState,
    offset: Offset,
): Boolean {
    Log.i(TAG, "hasNotTravelledEnough: ${abs(offset.x)} < ${state.maxWidth / 3}")
    // dictates how far the item needs to be swiped to be considered as swiped
    return abs(offset.x) < state.maxWidth / 3
}

