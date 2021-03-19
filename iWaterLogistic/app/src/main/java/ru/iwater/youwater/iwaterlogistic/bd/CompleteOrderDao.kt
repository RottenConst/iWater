package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder
import ru.iwater.youwater.iwaterlogistic.domain.Order

@Dao
interface CompleteOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(completeOrder: CompleteOrder)

    @Update
    fun update(completeOrder: CompleteOrder)

    @Delete
    fun delete(completeOrder: CompleteOrder)

    @Query("SELECT * FROM `completeorder` WHERE date IS :date ORDER BY timeComplete" )
    fun load(date: String): List<CompleteOrder>

    @Query("SELECT * FROM `completeorder` WHERE id IS :id" )
    fun getCompleteOrderById(id: Int): CompleteOrder
}