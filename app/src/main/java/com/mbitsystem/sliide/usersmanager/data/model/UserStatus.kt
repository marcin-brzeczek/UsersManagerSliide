package com.mbitsystem.sliide.usersmanager.data.model

sealed class UserStatus {
    object Active : UserStatus()
    object Inactive : UserStatus()

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}
