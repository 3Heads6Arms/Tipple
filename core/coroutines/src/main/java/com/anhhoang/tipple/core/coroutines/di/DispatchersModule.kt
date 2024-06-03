package com.anhhoang.tipple.core.coroutines.di

import com.anhhoang.tipple.core.coroutines.BlockingContext
import com.anhhoang.tipple.core.coroutines.LightweightContext
import com.anhhoang.tipple.core.coroutines.MainContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

/**
 * Module for providing coroutine dispatchers. This helps with managing dispatchers and testing more
 * flexibly.
 */
@InstallIn(SingletonComponent::class)
@Module
class DispatchersModule {

    @MainContext
    @Provides
    @Singleton
    fun providesMainContext(): CoroutineContext = Dispatchers.Main

    @BlockingContext
    @Provides
    @Singleton
    fun providesBlockingContext(): CoroutineContext = Dispatchers.IO

    @LightweightContext
    @Provides
    @Singleton
    fun providesLightweightContext(): CoroutineContext = Dispatchers.Default
}