package ch.uzh.ifi.accesscomplete.reports.API

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.uzh.ifi.accesscomplete.reports.ReportConverters

@Database(entities = [UzhQuest2::class], version = 1, exportSchema = false)
@TypeConverters(ReportConverters::class)
abstract class UzhQuest2DB: RoomDatabase() {
    abstract fun uzhQuest2Dao(): UzhQuestDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time. Kotlin object keyword declares a singleton class.
        @Volatile
        private var INSTANCE: UzhQuest2DB? = null

        fun getDatabase(context: Context): UzhQuest2DB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UzhQuest2DB::class.java,
                    "uzhQuest2_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
