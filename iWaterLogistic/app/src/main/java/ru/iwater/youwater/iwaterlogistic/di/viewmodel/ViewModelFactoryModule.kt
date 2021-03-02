package ru.iwater.youwater.iwaterlogistic.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen
import ru.iwater.youwater.iwaterlogistic.domain.AccountViewModel
import ru.iwater.youwater.iwaterlogistic.domain.OrderListViewModel

@Module
abstract class ViewModelFactoryModule {

    @Binds
    @OnScreen
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderListViewModel::class)
    abstract fun bindOrderListViewModel(orderListViewModel: OrderListViewModel): ViewModel
}