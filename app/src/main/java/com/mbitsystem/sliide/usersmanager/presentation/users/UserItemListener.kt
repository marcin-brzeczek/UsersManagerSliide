package com.mbitsystem.sliide.usersmanager.presentation.users

import com.mbitsystem.sliide.usersmanager.data.model.User

interface UserItemListener {
    fun onUserLongClicked(user: User) : Boolean
}
