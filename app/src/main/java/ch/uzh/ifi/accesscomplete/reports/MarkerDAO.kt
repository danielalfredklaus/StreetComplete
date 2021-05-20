package ch.uzh.ifi.accesscomplete.reports

import androidx.room.*
import ch.uzh.ifi.accesscomplete.reports.API.Marker
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDAO {

    @Query("SELECT * FROM marker")
    fun getAll(): Flow<List<Marker>>

    @Query("SELECT * FROM marker WHERE markerid IN (:markerIDs)")
    fun loadAllByIds(markerIDs: IntArray): Flow<List<Marker>>

    @Query("SELECT * FROM marker WHERE markerid LIKE :ID")
    fun findByID(ID: Int): Flow<Marker>

    @Query("SELECT * FROM marker WHERE bb_type LIKE :barrierType")
    fun findByBarrierType(barrierType: String): Flow<List<Marker>>

    @Insert
    suspend fun insertAll(vararg reports: Marker)

    @Update
    suspend fun updateMarkers(vararg reports: Marker)

    @Delete
    suspend fun delete(report: Marker)

    @Delete
    suspend fun delete(vararg reports: Marker)
}
