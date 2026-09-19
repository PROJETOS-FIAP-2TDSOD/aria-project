package com.fiap.ariachallenge.di

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.AriaMockApiInterceptor
import com.fiap.ariachallenge.data.remote.BearerTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        bearerTokenInterceptor: BearerTokenInterceptor,
        mockInterceptor: AriaMockApiInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(bearerTokenInterceptor)
            .addInterceptor(mockInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://aria-mock.api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAriaApiService(retrofit: Retrofit): AriaApiService =
        retrofit.create(AriaApiService::class.java)
}
