package com.mbitsystem.sliide.usersmanager.presentation.users

import android.app.Application
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import com.mbitsystem.sliide.usersmanager.BR
import com.mbitsystem.sliide.usersmanager.R
import com.mbitsystem.sliide.usersmanager.data.model.User
import com.mbitsystem.sliide.usersmanager.presentation.base.BaseViewModel
import com.mbitsystem.sliide.usersmanager.presentation.base.Event
import com.mbitsystem.sliide.usersmanager.presentation.users.paging.UserDataSourceFactory
import com.mbitsystem.sliide.usersmanager.usecases.AddUserUseCase
import com.mbitsystem.sliide.usersmanager.usecases.GetErroDialogDataUseCase
import com.mbitsystem.sliide.usersmanager.usecases.GetUsersUseCase
import com.mbitsystem.sliide.usersmanager.usecases.RemoveUserUseCase
import io.reactivex.rxkotlin.subscribeBy
import me.tatarka.bindingcollectionadapter2.OnItemBind
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Inject

class UsersViewModel @Inject constructor(
    app: Application,
    private val getUsersUseCase: GetUsersUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val removeUserUseCase: RemoveUserUseCase,
    private val getErroDialogDataUseCase: GetErroDialogDataUseCase,
    private val userDataSourceFactory: UserDataSourceFactory
) : BaseViewModel(app), UserItemListener {

    private var page = 0
    private var isLoadingMore = false
    private var currentSelectedUser: User? = null
    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(PAGINATION_INITIAL_LOAD_SIZE_HINT)
        .setPageSize(PAGINATION_PAGES_SIZE).build()

    val pagedList: LiveData<PagedList<User>> =
        LivePagedListBuilder(userDataSourceFactory, pagedListConfig)
            .setFetchExecutor(Executors.newFixedThreadPool(PAGINATION_FETCH_EXECUTOR_THREADS_LIMIT))
            .build()

    val itemBinding = OnItemBind<User> { itemBinding, _, _ ->
        itemBinding.set(BR.user, R.layout.item_user)
        itemBinding.bindExtra(BR.userItemClickedListener, this)
    }

    val userDiff: DiffUtil.ItemCallback<User> = User.DIFF_CALLBACK
    val state = MutableLiveData<UsersState>()
    val event = MutableLiveData<Event<UsersEvent>>()

    sealed class UsersState(
        open val showReloadButton: Boolean,
        open val showProgressBar: Boolean
    ) {
        object Loading : UsersState(showReloadButton = false, showProgressBar = true)
        data class Success(val users: List<User> = emptyList(),  val currentPage: Int? = null) :
            UsersState(showReloadButton = false, showProgressBar = false)

        data class Error(val error: Throwable, override val showReloadButton: Boolean) :
            UsersState(showReloadButton = showReloadButton, showProgressBar = false)
    }

    sealed class UsersEvent {
        object ShowUserRemovingConfirmationDialog : UsersEvent()
        object ShowUserAddingConfirmationDialog : UsersEvent()
        object ShowUserSuccessfullyAddedToast : UsersEvent()
        data class ShowErrorDialog(@StringRes val title: Int, @StringRes val subtitle: Int) : UsersEvent()
    }

    init {
        loadInitial()
    }

    @VisibleForTesting
    fun loadInitial() {
        getUsersUseCase(GetUsersUseCase.LaunchType.Initial)
            .doOnSubscribe { state.postValue(UsersState.Loading) }
            .subscribeBy(
                onSuccess = { usersData ->
                    val (users, currentPage) = usersData
                    userDataSourceFactory.submitItems(users)
                    setPage(currentPage - 1)
                    state.value = UsersState.Success(users, currentPage)
                },
                onError = { error ->
                    Timber.e(error, "Error loading first users page")
                    state.value = UsersState.Error(error, true)
                    handleErrors(error)
                }
            )
            .addToDisposables()
    }

    fun loadMore() {
        if (page != 0 && !isLoadingMore) {
            setLoadingMoreStatus(true)
            getUsersUseCase(GetUsersUseCase.LaunchType.NextPage(nextPage = page))
                .doOnSubscribe { state.postValue(UsersState.Loading) }
                .subscribeBy(
                    onSuccess = { usersData ->
                        val (users) = usersData
                        state.value = UsersState.Success(users, page)
                        userDataSourceFactory.addItems(users)
                        setPage(page - 1)
                        setLoadingMoreStatus(false)
                    },
                    onError = { error ->
                        Timber.e(error, "Error loading next users page")
                        setLoadingMoreStatus(false)
                        state.value = UsersState.Error(error, true)
                        handleErrors(error)
                    }
                )
                .addToDisposables()
        }
    }

    override fun onUserLongClicked(user: User): Boolean {
        currentSelectedUser = user
        event.value = Event(UsersEvent.ShowUserRemovingConfirmationDialog)
        return true
    }

    fun onReloadDataClicked() {
        if (page == 0) {
            loadInitial()
        } else {
            loadMore()
        }
    }

    fun onAddUserClicked() {
        event.value = Event(UsersEvent.ShowUserAddingConfirmationDialog)
    }

    fun onConfirmAddingUser(name: String, email: String, gender: String) {
        addUserUseCase(name, email, gender)
            .doOnSubscribe { state.postValue(UsersState.Loading) }
            .subscribeBy(
                onComplete = {
                    state.value = UsersState.Success()
                    event.value = Event(UsersEvent.ShowUserSuccessfullyAddedToast)
                },
                onError = { error ->
                    Timber.e(error, "Error during adding new User")
                    state.value = UsersState.Error(error, false)
                    handleErrors(error)
                }
            )
            .addToDisposables()
    }

    fun onConfirmRemovingUser() {
        currentSelectedUser?.let { user ->
            removeUserUseCase(id = user.id)
                .subscribeBy(
                    onComplete = {
                        userDataSourceFactory.removeItemAndInvalidate(user)
                    },
                    onError = { error ->
                        Timber.e(error, "Error during removing existing User")
                        state.value = UsersState.Error(error, false)
                        handleErrors(error)
                    }
                )
                .addToDisposables()

        } ?: Timber.e(NullPointerException("Selected user is null!"))
    }

    private fun handleErrors(error: Throwable) {
        val (title, subtitle) = getErroDialogDataUseCase(error)
        event.value = Event(UsersEvent.ShowErrorDialog(title, subtitle))
    }

    @VisibleForTesting
    fun setPage(page: Int) {
        this.page = page
    }

    @VisibleForTesting
    fun setLoadingMoreStatus(isLoadingMore: Boolean) {
        this.isLoadingMore = isLoadingMore
    }

    companion object {
        private const val PAGINATION_FETCH_EXECUTOR_THREADS_LIMIT = 5
        private const val PAGINATION_PAGES_SIZE = 10
        private const val PAGINATION_INITIAL_LOAD_SIZE_HINT = 10

    }
}
