package com.mbitsystem.sliide.usersmanager.usecases

import com.mbitsystem.sliide.usersmanager.data.model.User
import com.mbitsystem.sliide.usersmanager.data.repository.UsersRepository
import com.mbitsystem.sliide.usersmanager.utils.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UsersRepository,
    private val scheduler: SchedulerProvider
) : (GetUsersUseCase.LaunchType) -> Single<GetUsersUseCase.UsersData> {

    sealed class LaunchType {
        object Initial : LaunchType()
        data class NextPage(val nextPage: Int) : LaunchType()
    }

    data class UsersData(
        val users: List<User>,
        val currentPage: Int = 0
    )

    override fun invoke(launchType: LaunchType): Single<UsersData> {
        return when (launchType) {
            LaunchType.Initial -> {
                repository.getPaginationMetadata()
                    .flatMap { usersWithMetadata ->
                        val totalPages = usersWithMetadata.metadata.paginationModel.totalPages
                        repository.getUsers(page = totalPages)
                            .map { users ->
                                UsersData(
                                    users = users.sortedBy { user ->  user.id },
                                    currentPage = totalPages
                                )
                            }

                    }
            }
            is LaunchType.NextPage -> {
                repository
                    .getUsers(page = launchType.nextPage)
                    .map { users ->
                        UsersData(
                            users = users.sortedBy { user ->  user.id }
                        )
                    }
            }
        }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
    }
}
