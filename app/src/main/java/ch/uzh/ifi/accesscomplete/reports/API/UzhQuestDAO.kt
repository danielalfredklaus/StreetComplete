package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UzhQuestDAO {

    @Query("SELECT * FROM UzhQuest2")
    suspend fun getAll(): List<UzhQuest2>

    @Query("SELECT * FROM UzhQuest2 WHERE mid IN (:markerIDs)")
    suspend fun loadAllByIds(markerIDs: Array<String>): List<UzhQuest2>

    @Query("SELECT * FROM UzhQuest2 WHERE mid LIKE :ID")
    suspend fun findByID(ID: String): UzhQuest2

    @Query("SELECT * FROM UzhQuest2 WHERE title LIKE :markerTitle")
    suspend fun findByTitle(markerTitle: String): List<UzhQuest2>

    @Insert
    suspend fun insertAll(vararg quest2: UzhQuest2)

    @Update
    suspend fun updateMarkers(vararg quest2: UzhQuest2)

    @Delete
    suspend fun delete(quest2: UzhQuest2)

    @Delete
    suspend fun delete(vararg quest2: UzhQuest2)
}
