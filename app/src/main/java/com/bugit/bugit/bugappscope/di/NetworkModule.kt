package com.bugit.bugit.bugappscope.di

import com.bugit.bugit.bugappscope.data.ApiServiceInterface
import com.bugit.bugit.bugappscope.data.TokenServiceInterface
import com.bugit.bugit.utils.Constants.BASE_URL
import com.bugit.bugit.utils.Constants.REFRESH_OAUTH_TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    @Named("TokenRetrofit")
    fun provideRetrofit(@Named("Token") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("RefreshToken")
    fun provideNoTokenRetrofit(@Named("NonToken") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(REFRESH_OAUTH_TOKEN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Singleton
    @Provides
    fun provideApiService(@Named("TokenRetrofit") retrofit: Retrofit): ApiServiceInterface {
        return retrofit.create(ApiServiceInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideTokenApiService(@Named("RefreshToken") retrofit: Retrofit): TokenServiceInterface {
        return retrofit.create(TokenServiceInterface::class.java)
    }


}