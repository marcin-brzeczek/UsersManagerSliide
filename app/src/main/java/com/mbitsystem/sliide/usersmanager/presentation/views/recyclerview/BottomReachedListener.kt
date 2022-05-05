package com.mbitsystem.sliide.usersmanager.presentation.views.recyclerview

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

private const val THRESHOLD = 5

class BottomReachedListener(
    private val recyclerView: RecyclerView,
    private val callback: () -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (isBottomReached()) {
            callback()
        }
    }

    private fun isBottomReached(): Boolean {
        val layoutManager = recyclerView.layoutManager

        val lastVisibleItem = when (layoutManager) {
            is LinearLayoutManager ->
                layoutManager.findLastVisibleItemPosition()
            is GridLayoutManager ->
                layoutManager.findLastVisibleItemPosition()
            is StaggeredGridLayoutManager ->
                layoutManager.findLastCompletelyVisibleItemPositions(null).last()
            else ->
                throw IllegalArgumentException("Not supported for custom layout manager")
        }

        val totalItemCount = layoutManager.itemCount
        return totalItemCount - lastVisibleItem < THRESHOLD
    }
}
