package ch.uzh.ifi.accesscomplete.reports

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Report::class], version = 1)
abstract class ReportDatabase: RoomDatabase() {
    abstract fun reportsDAO(): ReportDAO
    /*
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time. Kotlin object keyword declares a singleton class.
        @Volatile
        private var INSTANCE: ReportDatabase? = null

        fun getDatabase(context: Context): ReportDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReportDatabase::class.java,
                    "report_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    } */
}
