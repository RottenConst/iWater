package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder

@Dao
interface CompleteOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(completeOrder: CompleteOrder)

    @Update
    fun update(completeOrder: CompleteOrder)

    @Delete
    suspend fun delete(completeOrder: CompleteOrder)

    @Query("SELECT * FROM `completeorder` ORDER BY timeComplete" )
    fun load(): List<CompleteOrder>

    @Query("SELECT * FROM `completeorder`")
    fun loadAll(): List<CompleteOrder>

    @Query("SELECT * FROM `completeorder` WHERE id IS :id" )
    fun getCompleteOrderById(id: Int?): CompleteOrder

    @Query("SELECT count(id) FROM 'completeorder'")
    fun getCountCompleteOrder(): Int

    @Query("SELECT sum(cash) FROM 'completeorder' WHERE typeOfCash IS :typeOfCash")
    fun getSumCashOf(typeOfCash: String): Float

    @Query("SELECT sum(cash) FROM 'completeorder' ")
    fun getSumCashFull(): Float

    @Query("SELECT sum(tank) FROM 'completeorder' ")
    fun getTankOfOrders(): Int
}