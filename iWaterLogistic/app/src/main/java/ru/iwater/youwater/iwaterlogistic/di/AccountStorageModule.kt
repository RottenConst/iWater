package ru.iwater.youwater.iwaterlogistic.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.iwaterlogistic.di.components.OnApplication
import ru.iwater.youwater.iwaterlogistic.iteractor.AccountStorage
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount

@Module(includes = [ContextModule::class])
class AccountStorageModule {

    @Provides
    @OnApplication
    fun provideAccountStorage(context: Context): StorageStateAccount =
        AccountStorage(context)
}