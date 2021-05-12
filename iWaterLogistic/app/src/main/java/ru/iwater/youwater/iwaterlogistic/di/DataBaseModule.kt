package ru.iwater.youwater.iwaterlogistic.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.iwaterlogistic.di.components.OnApplication
import ru.iwater.youwater.iwaterlogistic.di.components.OnScreen

//@Module(includes = [ContextModule::class])
//class DataBaseModule {

//    @Provides
//    @OnApplication
//    fun provideDataBase(context: Context): IWaterDB =
//        Room.databaseBuilder(
//            context.applicationContext,
//            IWaterDB::class.java,
//            "database"
//        ).build()
//}