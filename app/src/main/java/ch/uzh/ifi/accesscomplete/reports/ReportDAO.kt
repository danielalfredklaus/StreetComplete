package ch.uzh.ifi.accesscomplete.reports

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDAO {

    @Query("SELECT * FROM reports")
    fun getAll(): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE rid IN (:reportIDs)")
    fun loadAllByIds(reportIDs: IntArray): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE rid LIKE :ID")
    fun findByID(ID: Int): Flow<Report>

    //@Query("SELECT * FROM reports WHERE :value IN (content) ")
    //fun findByContentValue(value: String): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE reportType LIKE :reportType")
    fun findByReportType(reportType: String): Flow<List<Report>>

    @Insert
    suspend fun insertAll(vararg reports: Report)

    @Update
    suspend fun updateReports(vararg reports: Report)

    @Delete
    suspend fun delete(report: Report)

    @Delete
    suspend fun delete(vararg reports: Report)
}
