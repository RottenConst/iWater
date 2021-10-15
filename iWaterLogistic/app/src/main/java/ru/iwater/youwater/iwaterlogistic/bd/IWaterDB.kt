package ru.iwater.youwater.iwaterlogistic.bd

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.iwater.youwater.iwaterlogistic.domain.*
import ru.iwater.youwater.iwaterlogistic.util.CoordinateConverter
import ru.iwater.youwater.iwaterlogistic.util.ProductConverter

@Database(version = 3,
    entities = [WaterOrder::class, CompleteOrder::class, ReportDay::class, Expenses::class],)
@TypeConverters(ProductConverter::class, CoordinateConverter::class)
abstract class IWaterDB : RoomDatabase() {
    abstract fun waterOrderDao(): WaterOrderDao
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
                    ).addMigrations(MIGRATION_1_2).build()
                }
            }
            return INSTANCE
        }

        fun getDestroyDataBase() {
            INSTANCE = null
        }
    }
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Expenses ADD COLUMN fileName TEXT DEFAULT null")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS 'order'")
    }
}

