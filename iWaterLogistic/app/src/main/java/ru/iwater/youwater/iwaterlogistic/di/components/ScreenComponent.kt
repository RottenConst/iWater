package ru.iwater.youwater.iwaterlogistic.di.components

import android.content.Context
import dagger.Component
import ru.iwater.youwater.iwaterlogistic.di.viewmodel.ViewModelFactoryModule
import ru.iwater.youwater.iwaterlogistic.iteractor.StorageStateAccount
import ru.iwater.youwater.iwaterlogistic.screens.login.LoginActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.StartWorkActivity
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete.FragmentCompleteOrderInfo
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete.FragmentCompleteOrders
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.complete.RestoreOrderFragment
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.AboutOrderFragment
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.FragmentCurrentOrders
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.current.ShipmentsFragment
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.FragmentListReport
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.ReportCheckFragment
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.report.ReportFragment
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.LoadDriveFragment
import ru.iwater.youwater.iwaterlogistic.screens.main.tab.start.StartFragment
import ru.iwater.youwater.iwaterlogistic.screens.map.MapsActivity
import ru.iwater.youwater.iwaterlogistic.screens.splash.LoadMapActivity


@OnScreen
@Component(dependencies = [AppComponent::class], modules = [ViewModelFactoryModule::class])
interface ScreenComponent {
        fun appContext(): Context
        fun accountStorage(): StorageStateAccount
        fun inject(loginActivity: LoginActivity)
        fun inject(mapsActivity: MapsActivity)
        fun inject(startWorkActivity: StartWorkActivity)
        fun inject(startFragment: StartFragment)
        fun inject(loadDriveFragment: LoadDriveFragment)
        fun inject(fragmentCurrentOrders: FragmentCurrentOrders)
        fun inject(aboutOrderFragment: AboutOrderFragment)
        fun inject(shipmentsFragment: ShipmentsFragment)
        fun inject(completeOrders: FragmentCompleteOrders)
        fun inject(completeOrders: FragmentCompleteOrderInfo)
        fun inject(reportFragment: ReportFragment)
        fun inject(fragmentListReport: FragmentListReport)
        fun inject(reportCheckFragment: ReportCheckFragment)
        fun inject(loadMapActivity: LoadMapActivity)
        fun inject(restoreOrderFragment: RestoreOrderFragment)
}