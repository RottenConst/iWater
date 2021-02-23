package ru.iwater.youwater.iwaterlogistic.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ru.iwater.youwater.iwaterlogistic.di.components.OnApplication

/**
 * Класс - модуль генерирующий зависимость, для поставки обьекта SharedPreferences в другие классы
 */
@Module(includes = [ContextModule::class])
class SharedPreferencesModule(val name: String) {

    @Provides
    @OnApplication
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
}