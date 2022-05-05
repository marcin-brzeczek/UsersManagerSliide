package com.mbitsystem.sliide.usersmanager.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mbitsystem.sliide.usersmanager.presentation.views.recyclerview.BottomReachedListener

@BindingAdapter("visibleOrGone")
fun visibleOrGone(view: View, visible: Boolean) {
    view.isVisible = visible
}

@BindingAdapter(value = ["onBottomReached"])
fun RecyclerView.onBottomReached(callback: () -> Unit) {
    addOnScrollListener(BottomReachedListener(this, callback))
}
