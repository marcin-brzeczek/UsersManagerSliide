package com.mbitsystem.sliide.usersmanager.usecases

import com.mbitsystem.sliide.usersmanager.data.repository.UsersRepository
import com.mbitsystem.sliide.usersmanager.utils.SchedulerProvider
import io.reactivex.Completable

import javax.inject.Inject

class RemoveUserUseCase @Inject constructor(
    private val repository: UsersRepository,
    private val scheduler: SchedulerProvider
) : (Long) -> Completable {

    override fun invoke(id:Long): Completable {
        return repository.deleteUser(id)
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
    }
}
