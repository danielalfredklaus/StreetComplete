package ch.uzh.ifi.accesscomplete.reports.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.uzh.ifi.accesscomplete.reports.ReportConverters

@Database(entities = [MapMarker::class], version = 1, exportSchema = false)
@TypeConverters(ReportConverters::class)
abstract class MarkerDatabase: RoomDatabase() {
    abstract fun markersDAO(): MarkerDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time. Kotlin object keyword declares a singleton class.
        @Volatile
        private var INSTANCE: MarkerDatabase? = null

        fun getDatabase(context: Context): MarkerDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarkerDatabase::class.java,
                    "marker_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
