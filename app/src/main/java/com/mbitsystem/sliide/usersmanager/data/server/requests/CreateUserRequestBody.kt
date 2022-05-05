package com.mbitsystem.sliide.usersmanager.data.server.requests

import com.mbitsystem.sliide.usersmanager.data.model.UserStatus
import com.squareup.moshi.Json

data class CreateUserRequestBody(
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "gender")  val gender: String,
    @Json(name = "status") val status: UserStatus = UserStatus.Active
)
