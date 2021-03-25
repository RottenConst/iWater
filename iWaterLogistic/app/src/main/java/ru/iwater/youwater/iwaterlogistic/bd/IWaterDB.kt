package ru.iwater.youwater.iwaterlogistic.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.Expenses
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay
import javax.inject.Inject

@Database(entities = [Order::class, CompleteOrder::class, ReportDay::class, Expenses::class], version = 1)
abstract class IWaterDB : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun completeOrderDao(): CompleteOrderDao
    abstract fun reportDayDao(): ReportDayDao
    abstract fun ExpensesDao(): ExpensesDao

    companion object {
        var INSTANCE: IWaterDB? = null

        fun getIWaterDB(context: Context): IWaterDB? {
            if (INSTANCE == null) {
                synchronized(IWaterDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        IWaterDB::class.java,
                        "database"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun getDestroyDataBase() {
            INSTANCE = null
        }
    }
}
