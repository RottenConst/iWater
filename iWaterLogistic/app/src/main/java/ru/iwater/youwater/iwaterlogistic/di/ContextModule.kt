package ru.iwater.youwater.iwaterlogistic.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.iwaterlogistic.di.components.OnApplication

@Module
class ContextModule(val context: Context) {

    @Provides
    @OnApplication
    fun provideContext(): Context = context.applicationContext
}