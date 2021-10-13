package ru.iwater.youwater.iwaterlogistic.bd

import androidx.room.*
import ru.iwater.youwater.iwaterlogistic.domain.ReportDay

@Dao
interface ReportDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(reportDay: ReportDay)

    @Update
    fun update(reportDay: ReportDay)

    @Delete
    fun delete(reportDay: ReportDay)

    @Query("SELECT * FROM `reportday` WHERE date is :date" )
    fun loadReportDays(date: String): ReportDay

    @Query("SELECT * FROM `reportday` ORDER BY 'date'" )
    fun loadAllReports(): List<ReportDay>
}