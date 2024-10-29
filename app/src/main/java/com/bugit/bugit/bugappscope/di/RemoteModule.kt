package com.bugit.bugit.bugappscope.di

import com.bugit.bugit.bugappscope.data.ApiServiceInterface
import com.bugit.bugit.bugappscope.data.BugItRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideBugItRemoteRepository(apiServiceInterface: ApiServiceInterface): BugItRemoteRepository {
        return BugItRemoteRepository(apiServiceInterface)
    }
}