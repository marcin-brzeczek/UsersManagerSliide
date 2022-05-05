package com.mbitsystem.sliide.usersmanager.data.repository

import com.mbitsystem.sliide.usersmanager.data.model.User
import com.mbitsystem.sliide.usersmanager.data.model.PaginationMetaData
import com.mbitsystem.sliide.usersmanager.data.server.api.UsersApi
import com.mbitsystem.sliide.usersmanager.data.server.requests.CreateUserRequestBody
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersApi: UsersApi
) {

    fun getPaginationMetadata(): Single<PaginationMetaData> {
        return usersApi.getPaginationMetaData()
    }

    fun getUsers(page: Int): Single<List<User>> {
        return usersApi.getUsersNextPage(page)
    }

    fun deleteUser(userId: Long): Completable {
        return usersApi.removeUserById(userId)
    }

    fun addUser(name: String, email: String, gender: String): Single<User> {
        return usersApi.addUser(
            CreateUserRequestBody(
                name = name,
                email = email,
                gender = gender
            )
        )
    }
}
