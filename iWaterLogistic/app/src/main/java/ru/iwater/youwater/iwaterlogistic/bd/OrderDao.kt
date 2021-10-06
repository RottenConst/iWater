package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import ru.iwater.youwater.iwaterlogistic.domain.Order
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location

@Dao
interface OrderDao {
    @Insert(onConflict = REPLACE)
    suspend fun saveAll(orders: List<Order>)

    @Insert(onConflict = REPLACE)
    suspend fun save(order: Order)

    @Update
    suspend fun update(order: Order)

    @Query("UPDATE `order` SET num=:num WHERE id = :idOrder")
    suspend fun updateNum(num: Int, idOrder: Int)

    @Query("UPDATE `order` SET location=:location WHERE id = :idOrder")
    suspend fun updateLocation(location: Location?, idOrder: Int)

    @Delete
    fun delete(order: Order)

    @Query("SELECT * FROM `order` ORDER BY time" )
    fun load(): List<Order>

    @Query("SELECT * FROM `order` WHERE status IS 0 ORDER BY time" )
    fun getLoadCurrentOrder(): List<Order>
//
    @Query("SELECT * FROM 'Order' WHERE id IS :id")
    fun getOrderOnId(id: Int): Order
}