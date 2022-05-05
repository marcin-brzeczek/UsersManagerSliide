package com.mbitsystem.sliide.usersmanager.data.server.moshi_adapters

import com.mbitsystem.sliide.usersmanager.data.model.UserStatus
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

object UserStatusAdapter {

    @ToJson
    fun toJson(status: UserStatus): String = when (status) {
        UserStatus.Active -> "active"
        UserStatus.Inactive-> "inactive"
    }

    @FromJson
    fun fromJson(status: String): UserStatus = when (status) {
        "active" -> UserStatus.Active
         else -> UserStatus.Inactive
    }

}
