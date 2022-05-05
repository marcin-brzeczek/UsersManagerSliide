package com.mbitsystem.sliide.usersmanager.utils

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mbitsystem.sliide.usersmanager.presentation.base.BaseActivity

inline fun <reified T : ViewModel> Activity.getViewModel(): T {
    val viewModelFactory = (this as BaseActivity).viewModelFactory
    return ViewModelProvider(this, viewModelFactory)[T::class.java]
}
