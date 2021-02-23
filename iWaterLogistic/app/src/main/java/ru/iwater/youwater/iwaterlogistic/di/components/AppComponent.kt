package ru.iwater.youwater.iwaterlogistic.di.components

import android.content.Context
import dagger.Component
import ru.iwater.youwater.iwaterlogistic.di.AccountStorageModule
import ru.iwater.youwater.iwaterlogistic.di.ContextModule
import ru.iwater.youwater.iwaterlogistic.di.SharedPreferencesModule
import ru.iwater.youwater.iwaterlogistic.di.viewmodel.ViewModelFactoryModule
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount
import ru.iwater.youwater.iwaterlogistic.screens.login.LoginActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.MainActivity

@OnApplication
@Component(modules = [ContextModule::class, AccountStorageModule::class, SharedPreferencesModule::class])
interface AppComponent {
    fun accountStorage(): StorageStateAccount
    fun appContext(): Context
}