package com.mbitsystem.sliide.usersmanager.data.server.moshi_adapters

import com.mbitsystem.sliide.usersmanager.data.model.GenderType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

object GenderTypeAdapter {

    @ToJson
    fun toJson(gender: GenderType): String = when (gender) {
        GenderType.Male -> "male"
        GenderType.Female -> "female"
    }

    @FromJson
    fun fromJson(gender: String): GenderType = when (gender) {
        "male" -> GenderType.Male
        else -> GenderType.Female
    }
}
