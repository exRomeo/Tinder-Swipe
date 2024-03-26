package com.exromeo.myapplication

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.exromeo.myapplication.swipeable_item_lib.MatchProfileUiModel
import com.exromeo.myapplication.swipeable_item_lib.profiles


const val TAG = "TAG"

class SwipesViewModel : ViewModel() {

    val list: SnapshotStateList<MatchProfileUiModel> = mutableStateListOf()

    init {
        loadList()
    }

    fun loadList() {
        list.clear()
        list.addAll(
            profiles.reversed().mapIndexed { index, matchProfile ->
                MatchProfileUiModel(
                    matchProfile.name,
                    matchProfile.drawableResId,
                    onSwipeLeft = {
                        Log.i(TAG, "onSwipeLeft: $index")
                        removeAt(index)
                    },
                    onSwipeRight = {
                        Log.i(TAG, "onSwipeRight: $index")
                        removeAt(index)
                    }
                )
            })
    }

    private fun removeAt(index: Int) {
        Log.i(TAG, "removeAt: $index, size: ${list.size}")
        if (index !in list.indices) return
        list.removeAt(index)
    }
}