package com.mbitsystem.sliide.usersmanager.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mbitsystem.sliide.usersmanager.di.InjectingViewModelFactory
import com.mbitsystem.sliide.usersmanager.di.ViewModelKey
import com.mbitsystem.sliide.usersmanager.presentation.users.UsersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UsersViewModel::class)
    abstract fun bindUsersViewModel(viewModel: UsersViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: InjectingViewModelFactory): ViewModelProvider.Factory
}
