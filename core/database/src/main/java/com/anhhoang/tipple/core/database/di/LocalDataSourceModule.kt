package com.anhhoang.tipple.core.database.di

import com.anhhoang.tipple.core.database.TippleLocalDataSource
import com.anhhoang.tipple.core.database.room.TippleLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface LocalDataSourceModule {
    @Binds
    fun bindTippleLocalDataSource(impl: TippleLocalDataSourceImpl): TippleLocalDataSource
}
