package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import ru.iwater.youwater.iwaterlogistic.domain.Expenses

@Dao
interface ExpensesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(expenses: Expenses)

    @Update
    fun update(expenses: Expenses)

    @Delete
    fun delete(expenses: Expenses)

    @Query("SELECT * FROM `expenses` WHERE date IS :date " )
    fun load(date:String): List<Expenses>

    @Query("SELECT sum(money) FROM `expenses` WHERE date IS :date " )
    fun sumCost(date:String): Float
}