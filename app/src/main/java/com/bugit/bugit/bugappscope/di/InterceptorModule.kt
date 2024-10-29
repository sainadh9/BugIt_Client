package com.bugit.bugit.bugappscope.di

import android.content.Context
import com.bugit.bugit.bugappscope.data.TokenServiceInterface
import com.bugit.bugit.bugappscope.network.TokenInterceptor
import com.bugit.bugit.local.BugPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class InterceptorModule {

    @Provides
    @Singleton
    fun provideTokenInterceptor(
        tokenServiceInterface: TokenServiceInterface,
        bugPref: BugPref,
        @ApplicationContext context: Context
    ): TokenInterceptor {
        return TokenInterceptor(tokenServiceInterface, bugPref, context)
    }


    @Provides
    @Singleton
    @Named("Token")
    fun provideTokenOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(tokenInterceptor) // Attach the custom interceptor
            .build()
    }

    @Provides
    @Singleton
    @Named("NonToken")
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}





