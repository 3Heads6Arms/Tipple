package com.anhhoang.tipple.core.data.di

import com.anhhoang.tipple.core.data.repository.TippleRepositoryImpl
import com.anhhoang.tipple.core.data.repository.TippleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    fun bindTippleRepository(tippleCocktailDbRepository: TippleRepositoryImpl): TippleRepository

}