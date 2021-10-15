package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import ru.iwater.youwater.iwaterlogistic.domain.WaterOrder

@Dao
interface WaterOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(orders: List<WaterOrder>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(waterOrder: WaterOrder)

    @Update
    suspend fun update(waterOrder: WaterOrder)

    @Query("UPDATE WaterOrder SET num=:num WHERE order_id = :idOrder")
    suspend fun updateNum(num: String, idOrder: Int)

    @Query("UPDATE WaterOrder SET coords=:location WHERE order_id = :idOrder")
    suspend fun updateLocation(location: String?, idOrder: Int)

    @Delete
    fun delete(waterOrder: WaterOrder)

    @Query("SELECT * FROM WaterOrder ORDER BY time")
    fun load(): List<WaterOrder>

    @Query("SELECT * FROM WaterOrder WHERE status IS 0 ORDER BY time")
    fun getLoadCurrentOrder(): List<WaterOrder>
    //
    @Query("SELECT * FROM WaterOrder WHERE order_id IS :id")
    fun getOrderOnId(id: Int): WaterOrder
}