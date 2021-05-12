package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import ru.iwater.youwater.iwaterlogistic.domain.CompleteOrder

//@Dao
//interface CompleteOrderDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun save(completeOrder: CompleteOrder)
//
//    @Update
//    fun update(completeOrder: CompleteOrder)
//
//    @Delete
//    fun delete(completeOrder: CompleteOrder)
//
//    @Query("SELECT * FROM `completeorder` WHERE date IS :date ORDER BY timeComplete" )
//    fun load(date: String): List<CompleteOrder>
//
//    @Query("SELECT * FROM `completeorder`")
//    fun loadAll(): List<CompleteOrder>
//
//    @Query("SELECT * FROM `completeorder` WHERE id IS :id" )
//    fun getCompleteOrderById(id: Int?): CompleteOrder
//
//    @Query("SELECT count(id) FROM 'completeorder' WHERE date IS :date")
//    fun getCountCompleteOrder(date: String): Int
//
//    @Query("SELECT sum(cash) FROM 'completeorder' WHERE typeOfCash IS :typeOfCash AND date IS :date")
//    fun getSumCashOf(typeOfCash: String, date: String): Float
//
//    @Query("SELECT sum(cash) FROM 'completeorder' WHERE date IS :date")
//    fun getSumCashFull(date: String): Float
//
//    @Query("SELECT sum(tank) FROM 'completeorder' WHERE date IS :date")
//    fun getTankOfOrders(date: String): Int
//}