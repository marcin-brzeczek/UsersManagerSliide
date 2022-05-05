package com.mbitsystem.sliide.usersmanager.presentation.users

import androidx.lifecycle.Observer
import com.mbitsystem.sliide.usersmanager.ErrorResponseMock
import com.mbitsystem.sliide.usersmanager.InstantExecutorExtension
import com.mbitsystem.sliide.usersmanager.R
import com.mbitsystem.sliide.usersmanager.data.model.GenderType
import com.mbitsystem.sliide.usersmanager.data.model.User
import com.mbitsystem.sliide.usersmanager.data.model.UserStatus
import com.mbitsystem.sliide.usersmanager.presentation.base.Event
import com.mbitsystem.sliide.usersmanager.presentation.users.paging.UserDataSourceFactory
import com.mbitsystem.sliide.usersmanager.usecases.AddUserUseCase
import com.mbitsystem.sliide.usersmanager.usecases.GetErroDialogDataUseCase
import com.mbitsystem.sliide.usersmanager.usecases.GetUsersUseCase
import com.mbitsystem.sliide.usersmanager.usecases.RemoveUserUseCase
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.HttpURLConnection
import java.net.UnknownHostException

@ExtendWith(InstantExecutorExtension::class)
internal class UsersViewModelTest {

    private lateinit var viewModel: UsersViewModel

    private var stateObserver = spyk(Observer<UsersViewModel.UsersState> {})
    private var eventObserver = spyk(Observer<Event<UsersViewModel.UsersEvent>> {})

    private val getErrorDialogDataUseCase = GetErroDialogDataUseCase()
    private val userDataSource =  UserDataSourceFactory()
    private val getUsersUseCase: GetUsersUseCase = mockk()
    private val addUsersUseCase: AddUserUseCase = mockk()
    private val removeUsersUseCase: RemoveUserUseCase = mockk()

    @BeforeEach
    fun setUp() {
        every { getUsersUseCase(GetUsersUseCase.LaunchType.Initial) } returns Single.just(
            GetUsersUseCase.UsersData(users = TEST_USERS, currentPage = 1)
        )
        viewModel = UsersViewModel(
            app = mockk(),
            getUsersUseCase = getUsersUseCase,
            addUserUseCase = addUsersUseCase,
            removeUserUseCase = removeUsersUseCase,
            getErroDialogDataUseCase = getErrorDialogDataUseCase,
            userDataSourceFactory = userDataSource
        )
        stateObserver = spyk(Observer { })
        eventObserver = spyk(Observer { })
        viewModel.state.observeForever(stateObserver)
        viewModel.event.observeForever(eventObserver)
    }

    @AfterEach
    fun after() {
        viewModel.state.removeObserver(stateObserver)
        viewModel.event.removeObserver(eventObserver)
    }

    @Test
    fun `when fetched successfully initial page then returns success state`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        every { getUsersUseCase(GetUsersUseCase.LaunchType.Initial) } returns Single.just(
            GetUsersUseCase.UsersData(users = TEST_USERS, currentPage = TEST_PAGE)
        )

        // When
        viewModel.loadInitial()

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Success)
            assertTrue((this as UsersViewModel.UsersState.Success).currentPage == TEST_PAGE)
            assertFalse(this.showReloadButton)
        }
    }

    @Test
    fun `when missing permissions during fetching initial page then display error dialog with correct message`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        val event = slot<Event<UsersViewModel.UsersEvent>>()
        every { getUsersUseCase(GetUsersUseCase.LaunchType.Initial) } returns Single.error(
            ErrorResponseMock.createHttpError(HttpURLConnection.HTTP_FORBIDDEN)
        )

        // When
        viewModel.loadInitial()

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Error)
            assertTrue(this.showReloadButton)
        }
        verify { eventObserver.onChanged(capture(event)) }
        event.captured.peekContent().run {
            val errorDialog = this as UsersViewModel.UsersEvent.ShowErrorDialog
            assertTrue(this == errorDialog)
            assertTrue(errorDialog.subtitle == R.string.error_dialog_authorization_subtitle)
        }
    }

    @Test
    fun `when fetched successfully next page then returns success state`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        viewModel.setPage(TEST_PAGE)
        viewModel.setLoadingMoreStatus(false)
        every { getUsersUseCase(GetUsersUseCase.LaunchType.NextPage(nextPage = TEST_PAGE)) } returns Single.just(
            GetUsersUseCase.UsersData(users = TEST_USERS)
        )

        // When
        viewModel.loadMore()

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Success)
        }
    }

    @Test
    fun `when server error during fetching next page then display error dialog with correct message`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        val event = slot<Event<UsersViewModel.UsersEvent>>()
        viewModel.setPage(TEST_PAGE)
        viewModel.setLoadingMoreStatus(false)
        @Suppress("DEPRECATION")
        every { getUsersUseCase(GetUsersUseCase.LaunchType.NextPage(nextPage = TEST_PAGE)) } returns Single.error(
            ErrorResponseMock.createHttpError(HttpURLConnection.HTTP_SERVER_ERROR)
        )

        // When
        viewModel.loadMore()

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Error)
            assertTrue(this.showReloadButton)
        }
        verify { eventObserver.onChanged(capture(event)) }
        event.captured.peekContent().run {
            val errorDialog = this as UsersViewModel.UsersEvent.ShowErrorDialog
            assertTrue(this == errorDialog)
            assertTrue(errorDialog.subtitle == R.string.error_dialog_server_subtitle)
        }
    }

    @Test
    fun `when user has been inserted successfully then display toast`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        val event = slot<Event<UsersViewModel.UsersEvent>>()
        val (_, name, email, gender) = TEST_USER
        every { addUsersUseCase(name, email, gender.toString()) } returns Completable.complete()

        // When
        viewModel.onConfirmAddingUser(name, email, gender.toString())

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Success)
        }
        verify { eventObserver.onChanged(capture(event)) }
        event.captured.peekContent().run {
            assertTrue(this == UsersViewModel.UsersEvent.ShowUserSuccessfullyAddedToast)
        }
    }

    @Test
    fun `when user validation failed then display error dialog with correct message`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        val event = slot<Event<UsersViewModel.UsersEvent>>()
        val (_, name, email, gender) = TEST_USER
        every { addUsersUseCase(name, email, gender.toString()) } returns Completable.error(
            ErrorResponseMock.createHttpError(HTTP_ERROR_VALIDATION_CODE)
        )

        // When
        viewModel.onConfirmAddingUser(name, email, gender.toString())

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Error)
        }
        verify { eventObserver.onChanged(capture(event)) }
        event.captured.peekContent().run {
            val errorDialog = this as UsersViewModel.UsersEvent.ShowErrorDialog
            assertTrue(this == errorDialog)
            assertTrue(errorDialog.subtitle == R.string.error_dialog_validation_subtitle)
        }
    }

    @Test
    fun `when user has been removed successfully then return success state`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        every { removeUsersUseCase(id = TEST_USER.id) } returns Completable.complete()

        // When
        viewModel.onConfirmRemovingUser()

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Success)
        }
    }

    @Test
    fun `when no internet connetion during removing user then display error dialog with correct message`() {
        // Given
        val state = slot<UsersViewModel.UsersState>()
        val event = slot<Event<UsersViewModel.UsersEvent>>()
        every { removeUsersUseCase(id = TEST_USER.id) } returns Completable.error(UnknownHostException())
        viewModel.onUserLongClicked(TEST_USER)

        // When
        viewModel.onConfirmRemovingUser()

        // Then
        verify { stateObserver.onChanged(capture(state)) }
        state.captured.run {
            assertTrue(this is UsersViewModel.UsersState.Error)
        }
        verify { eventObserver.onChanged(capture(event)) }
        event.captured.peekContent().run {
            val errorDialog = this as UsersViewModel.UsersEvent.ShowErrorDialog
            assertTrue(this == errorDialog)
            assertTrue(errorDialog.subtitle == R.string.error_dialog_no_internet_subtitle)
        }
    }


    @Test
    fun `when long click on item user performed then display dialog with removing user question`() {
        // Given
        val event = slot<Event<UsersViewModel.UsersEvent>>()

        // When
        viewModel.onUserLongClicked(TEST_USER)

        // Then
        verify { eventObserver.onChanged(capture(event)) }
        event.captured.peekContent().run {
            assertTrue(this is UsersViewModel.UsersEvent.ShowUserRemovingConfirmationDialog)
        }
    }

    @Test
    fun `when add user click performed then display dialog with entering new user data`() {
        // Given
        val event = slot<Event<UsersViewModel.UsersEvent>>()

        // When
        viewModel.onAddUserClicked()

        // Then
        verify { eventObserver.onChanged(capture(event)) }
        event.captured.peekContent().run {
            assertTrue(this is UsersViewModel.UsersEvent.ShowUserAddingConfirmationDialog)
        }
    }


    private companion object {
        const val TEST_PAGE = 100
        const val USER_ID = 2000L
        val TEST_USER = User(
            USER_ID,
            "Jim Beam",
            "test@test.com",
            GenderType.Male,
            UserStatus.Active
        )
        val HTTP_ERROR_VALIDATION_CODE = 422
        val TEST_USERS = (0..10L).map { id -> TEST_USER.copy(id = id) }
    }
}
