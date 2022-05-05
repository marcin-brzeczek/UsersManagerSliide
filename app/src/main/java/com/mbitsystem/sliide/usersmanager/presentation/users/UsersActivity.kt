package com.mbitsystem.sliide.usersmanager.presentation.users

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.mbitsystem.sliide.usersmanager.R
import com.mbitsystem.sliide.usersmanager.databinding.ActivityUsersBinding
import com.mbitsystem.sliide.usersmanager.presentation.base.BaseActivity
import com.mbitsystem.sliide.usersmanager.presentation.base.EventObserver
import com.mbitsystem.sliide.usersmanager.presentation.views.dialogs.DialogWithNameAndEmailEntries
import com.mbitsystem.sliide.usersmanager.utils.getViewModel

class UsersActivity : BaseActivity() {

    private lateinit var binding: ActivityUsersBinding

    private val viewModel by lazy { getViewModel<UsersViewModel>() }

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        observeState()
        observeEvents()
    }

    private fun observeEvents() {
        viewModel.event.observe(this, EventObserver { event ->
            when (event) {
                UsersViewModel.UsersEvent.ShowUserAddingConfirmationDialog -> {
                    DialogWithNameAndEmailEntries(this)
                        .show(
                            title = R.string.users_toolbar_title,
                            onPositiveButtonClick = { name, email, gender ->
                                viewModel.onConfirmAddingUser(name, email, gender)
                            })
                }
                is UsersViewModel.UsersEvent.ShowUserRemovingConfirmationDialog -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.remove_user_dialog_title)
                        .setMessage(R.string.remove_user_dialog_subtitle)
                        .setCancelable(false)
                        .setPositiveButton(R.string.remove_user_dialog_button_confirm) { _, _ ->
                            viewModel.onConfirmRemovingUser()
                        }
                        .setNegativeButton(R.string.remove_user_dialog_button_cancel) { _, _ -> }
                        .displayIfNecessary()
                }
                UsersViewModel.UsersEvent.ShowUserSuccessfullyAddedToast -> {
                    Snackbar.make(binding.root, getString(R.string.user_successfully_added_toast), Snackbar.LENGTH_LONG)
                        .show()
                }
                is UsersViewModel.UsersEvent.ShowErrorDialog -> {
                    AlertDialog.Builder(this)
                        .setTitle(event.title)
                        .setMessage(event.subtitle)
                        .setPositiveButton(R.string.ok_button_title) { _, _ -> }
                        .setCancelable(false)
                        .displayIfNecessary()
                }
            }
        })
    }

    private fun AlertDialog.Builder.displayIfNecessary(){
        if(dialog == null || dialog?.isShowing == false) {
            dialog = this.create()
            dialog?.show()
        }
    }

    private fun observeState() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is UsersViewModel.UsersState.Success -> {
                    binding.pageNumber.text = state.currentPage?.let { page ->
                        getString(R.string.users_page_number, page)
                    }
                }
                else -> Unit
            }
        }
    }
}
