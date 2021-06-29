package ru.iwater.youwater.iwaterlogistic.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.util.CoordinateConverter
import ru.iwater.youwater.iwaterlogistic.util.ProductConverter

@Database(entities = [Order::class, CompleteOrder::class, ReportDay::class, Expenses::class], version = 1)
@TypeConverters(ProductConverter::class, CoordinateConverter::class)
abstract class IWaterDB : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun completeOrderDao(): CompleteOrderDao
    abstract fun reportDayDao(): ReportDayDao
    abstract fun ExpensesDao(): ExpensesDao
//
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
