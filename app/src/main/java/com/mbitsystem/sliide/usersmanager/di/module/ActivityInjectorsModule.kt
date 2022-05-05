package com.mbitsystem.sliide.usersmanager.di

import com.mbitsystem.sliide.usersmanager.presentation.users.UsersActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityInjectorsModule {

    @ContributesAndroidInjector
    abstract fun provideUsersActivityInjector(): UsersActivity
}
