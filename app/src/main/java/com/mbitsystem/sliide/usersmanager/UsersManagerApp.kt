package com.mbitsystem.sliide.usersmanager

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import com.mbitsystem.sliide.usersmanager.di.DaggerAppComponent
import timber.log.Timber

class UsersManagerApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
