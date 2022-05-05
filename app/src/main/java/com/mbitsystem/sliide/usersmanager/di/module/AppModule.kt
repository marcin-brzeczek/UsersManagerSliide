package com.challenge.omdb.di.module

import com.mbitsystem.sliide.usersmanager.utils.SchedulerProvider
import com.mbitsystem.sliide.usersmanager.utils.SchedulerProviderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider = SchedulerProviderImpl()
}
