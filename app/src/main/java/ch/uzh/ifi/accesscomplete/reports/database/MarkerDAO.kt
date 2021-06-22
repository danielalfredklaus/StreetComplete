package ch.uzh.ifi.accesscomplete.reports.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDAO {

    @Query("SELECT * FROM MapMarker")
    suspend fun getAll(): List<MapMarker>

    @Query("SELECT * FROM MapMarker WHERE nodeid IN (:markerIDs)")
    suspend fun loadAllByIds(markerIDs: IntArray): List<MapMarker>

    @Query("SELECT * FROM MapMarker WHERE nodeid LIKE :ID")
    suspend fun findByID(ID: Int): MapMarker

    @Query("SELECT * FROM MapMarker WHERE title LIKE :markerTitle")
    suspend fun findByTitle(markerTitle: String): List<MapMarker>

    @Insert
    suspend fun insertAll(vararg reports: MapMarker)

    @Update
    suspend fun updateMarkers(vararg reports: MapMarker)

    @Delete
    suspend fun delete(report: MapMarker)

    @Delete
    suspend fun delete(vararg reports: MapMarker)
}
