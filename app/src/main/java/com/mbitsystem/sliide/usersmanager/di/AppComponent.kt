package com.mbitsystem.sliide.usersmanager.di

import android.app.Application
import com.challenge.omdb.di.module.*
import com.mbitsystem.sliide.usersmanager.UsersManagerApp
import com.mbitsystem.sliide.usersmanager.di.module.NetworkModule
import com.mbitsystem.sliide.usersmanager.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        AndroidSupportInjectionModule::class,
        ActivityInjectorsModule::class,
        ViewModelModule::class,
        NetworkModule::class
   ]
)

interface AppComponent : AndroidInjector<UsersManagerApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
