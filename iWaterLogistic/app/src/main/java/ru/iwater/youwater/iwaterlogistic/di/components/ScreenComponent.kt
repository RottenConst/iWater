package ru.iwater.youwater.iwaterlogistic.di.components

import android.content.Context
import dagger.Component
import ru.iwater.youwater.iwaterlogistic.di.viewmodel.ViewModelFactoryModule
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount
import ru.iwater.youwater.iwaterlogistic.screens.cardOrder.AboutOrderFragment
import ru.iwater.youwater.iwaterlogistic.screens.cardOrder.ShipmentsFragment
import ru.iwater.youwater.iwaterlogistic.screens.login.LoginActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.FragmentCurrentOrders

@OnScreen
@Component(dependencies = [AppComponent::class], modules = [ViewModelFactoryModule::class])
interface ScreenComponent {
        fun appContext(): Context
        fun accountStorage(): StorageStateAccount
        fun inject(loginActivity: LoginActivity)
        fun inject(fragmentCurrentOrders: FragmentCurrentOrders)
        fun inject(aboutOrderFragment: AboutOrderFragment)
        fun inject(shipmentsFragment: ShipmentsFragment)
}