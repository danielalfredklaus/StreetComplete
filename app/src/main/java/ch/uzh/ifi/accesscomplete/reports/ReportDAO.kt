package ch.uzh.ifi.accesscomplete.reports

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDAO {
    /*
    @Query("SELECT * FROM reports")
    fun getAll(): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE rid IN (:reportIDs)")
    fun loadAllByIds(reportIDs: IntArray): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE rid LIKE :ID")
    fun findByID(ID: Int): Flow<Report>

    @Query("SELECT * FROM reports WHERE report_type LIKE :reportType")
    fun findByReportType(reportType: String): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE barrier_type LIKE :barrierType")
    fun findByBarrierType(barrierType: String): Flow<List<Report>>

    @Insert
    suspend fun insertAll(vararg reports: Report)

    @Update
    suspend fun updateReports(vararg reports: Report)

    @Delete
    suspend fun delete(report: Report) */
}
