package com.fiap.ariachallenge.di

import com.fiap.ariachallenge.data.remote.AriaApiService
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

// Ambiente de desenvolvimento local. Descomente a linha do emulador que voce esta usando
// e comente a outra — NAO deixe as duas descomentadas ao mesmo tempo.

// Emulador padrao do Android Studio (AVD): 10.0.2.2 e o alias para o localhost do PC host.
// private const val BASE_URL = "http://10.0.2.2:8080/"

// Genymotion: 10.0.3.2 e o gateway padrao dele para o PC host.
private const val BASE_URL = "http://10.0.3.2:8080/"

// Dispositivo fisico: troque pelo IP da maquina na rede local (ver network_security_config.xml).

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        bearerTokenInterceptor: BearerTokenInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(bearerTokenInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAriaApiService(retrofit: Retrofit): AriaApiService =
        retrofit.create(AriaApiService::class.java)
}