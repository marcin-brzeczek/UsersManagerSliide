package com.mbitsystem.sliide.usersmanager.data.server.api

import com.mbitsystem.sliide.usersmanager.BuildConfig
import com.mbitsystem.sliide.usersmanager.data.model.PaginationMetaData
import com.mbitsystem.sliide.usersmanager.data.model.User
import com.mbitsystem.sliide.usersmanager.data.server.requests.CreateUserRequestBody
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface UsersApi {

    @GET("public/v2/users")
    fun getUsersNextPage(@Query("page") page: Int): Single<List<User>>

    @GET("public/v1/users")
    fun getPaginationMetaData(): Single<PaginationMetaData>

    @Headers(AUTHORIZATION_HEADER)
    @POST("public/v2/users")
    fun addUser(@Body createUserRequestBody: CreateUserRequestBody): Single<User>

    @Headers(AUTHORIZATION_HEADER)
    @DELETE("public/v2/users/{userId}")
    fun removeUserById(@Path("userId") userId: Long): Completable

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization: Bearer ${BuildConfig.GOREST_API_KEY}"
    }
}
