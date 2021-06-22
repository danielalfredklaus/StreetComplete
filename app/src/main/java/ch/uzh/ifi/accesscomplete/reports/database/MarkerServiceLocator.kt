package ch.uzh.ifi.accesscomplete.reports.database

import android.content.Context
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest2DB
import ch.uzh.ifi.accesscomplete.reports.API.WebserverAccess

//https://developer.android.com/codelabs/android-room-with-a-view-kotlin#12
object MarkerServiceLocator {

    fun getRepo (context: Context): MarkerRepo{
        val mDB by lazy { MarkerDatabase.getDatabase(context) }
        val qDB by lazy { UzhQuest2DB.getDatabase(context) }
        val webserverAccess by lazy { WebserverAccess() }
        val markerRepo by lazy { MarkerRepo(mDB.markersDAO(), webserverAccess, qDB.uzhQuest2Dao()) }
        return markerRepo
    }

}
