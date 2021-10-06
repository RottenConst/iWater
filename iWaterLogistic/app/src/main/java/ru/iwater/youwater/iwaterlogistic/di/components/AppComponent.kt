package ru.iwater.youwater.iwaterlogistic.di.components

import android.content.Context
import dagger.Component
import ru.iwater.youwater.iwaterlogistic.bd.IWaterDB
import ru.iwater.youwater.iwaterlogistic.di.AccountStorageModule
import ru.iwater.youwater.iwaterlogistic.di.ContextModule
import ru.iwater.youwater.iwaterlogistic.di.DataBaseModule
import ru.iwater.youwater.iwaterlogistic.di.SharedPreferencesModule
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount

@OnApplication
@Component(modules = [ContextModule::class, DataBaseModule::class, AccountStorageModule::class, SharedPreferencesModule::class])
interface AppComponent {
    fun accountStorage(): StorageStateAccount
    fun dataBase(): IWaterDB
    fun appContext(): Context
}