package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import ru.iwater.youwater.iwaterlogistic.domain.OrderNewItem
import ru.iwater.youwater.iwaterlogistic.domain.mapdata.Location

@Dao
interface OrderNewItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(orders: List<OrderNewItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(order: OrderNewItem)

    @Update
    suspend fun update(order: OrderNewItem)

    @Query("UPDATE `OrderNewItem` SET num=:num WHERE order_id = :idOrder")
    suspend fun updateNum(num: String, idOrder: Int)

    @Query("UPDATE `OrderNewItem` SET coords=:location WHERE order_id = :idOrder")
    suspend fun updateLocation(location: String?, idOrder: Int)

    @Delete
    fun delete(order: OrderNewItem)

    @Query("SELECT * FROM `OrderNewItem` ORDER BY time" )
    fun load(): List<OrderNewItem>

    @Query("SELECT * FROM `OrderNewItem` WHERE status IS 0 ORDER BY time" )
    fun getLoadCurrentOrder(): List<OrderNewItem>
    //
    @Query("SELECT * FROM 'OrderNewItem' WHERE order_id IS :id")
    fun getOrderOnId(id: Int): OrderNewItem
}