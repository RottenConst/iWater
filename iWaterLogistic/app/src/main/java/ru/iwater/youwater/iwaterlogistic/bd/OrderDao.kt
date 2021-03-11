package ru.iwater.youwater.iwaterlogistic.bd

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import ru.iwater.youwater.iwaterlogistic.domain.Order

@Dao
interface OrderDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(order: Order)

    @Update
    fun update(order: Order)

    @Delete
    fun delete(order: Order)

    @Query("SELECT * FROM `order` ORDER BY timeEnd" )
    fun load(): List<Order>

    @Query("SELECT * FROM 'order' WHERE id IS :id")
    fun getOrderOnId(id: Int): Order
}