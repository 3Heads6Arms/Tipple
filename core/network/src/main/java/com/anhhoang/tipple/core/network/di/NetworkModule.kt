package com.anhhoang.tipple.core.network.di

import com.anhhoang.tipple.core.network.BuildConfig
import com.anhhoang.tipple.core.network.TippleNetworkDataSource
import com.anhhoang.tipple.core.network.retrofit.CocktailDbApi
import com.anhhoang.tipple.core.network.retrofit.CocktailDbNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Provides
    @Singleton
    internal fun json() = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    internal fun provideOkHttpCallFactory(): Call.Factory =
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
        }).build()

    @Provides
    @Singleton
    internal fun provideNetworkApi(json: Json, okHttpCallFactory: dagger.Lazy<Call.Factory>) =
        Retrofit.Builder().baseUrl(BuildConfig.COCKTAIL_DB_URL).callFactory { okHttpCallFactory.get().newCall(it) }
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
            .create(CocktailDbApi::class.java)
}

@InstallIn(SingletonComponent::class)
@Module
interface BindingsNetworkModule {
    @Binds
    fun bindNetworkDataSource(networkDataSource: CocktailDbNetworkDataSource): TippleNetworkDataSource
}
