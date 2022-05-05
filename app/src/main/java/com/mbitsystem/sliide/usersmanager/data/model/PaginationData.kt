package com.mbitsystem.sliide.usersmanager.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginationMetaData(
    @Json(name = "meta") val metadata: PaginationData,
)

@JsonClass(generateAdapter = true)
data class PaginationData(
    @Json(name = "pagination") val paginationModel: PaginationModel
)

@JsonClass(generateAdapter = true)
data class PaginationModel(
    @Json(name = "total")  val totalUsers: Int,
    @Json(name = "pages")  val totalPages: Int,
    @Json(name = "page")  val currentPage: Int,
    @Json(name = "limit")  val paginationLimit: Int
)
