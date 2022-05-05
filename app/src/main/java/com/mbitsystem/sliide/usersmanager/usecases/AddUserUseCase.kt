package com.mbitsystem.sliide.usersmanager.usecases

import com.mbitsystem.sliide.usersmanager.data.repository.UsersRepository
import com.mbitsystem.sliide.usersmanager.utils.SchedulerProvider
import io.reactivex.Completable
import javax.inject.Inject

class AddUserUseCase @Inject constructor(
    private val repository: UsersRepository,
    private val scheduler: SchedulerProvider
) : (String, String, String) -> Completable {

    override fun invoke(name: String, email: String, gender: String): Completable {
        return repository.addUser(name, email, gender)
            .ignoreElement()
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
    }
}
