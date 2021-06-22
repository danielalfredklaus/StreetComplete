package ch.uzh.ifi.accesscomplete.reports

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import ch.uzh.ifi.accesscomplete.map.QuestPinLayerManager
import ch.uzh.ifi.accesscomplete.map.QuestPinLayerManager_Factory
import ch.uzh.ifi.accesscomplete.reports.API.*
import ch.uzh.ifi.accesscomplete.reports.database.MarkerDAO
import ch.uzh.ifi.accesscomplete.reports.database.MarkerDatabase
import ch.uzh.ifi.accesscomplete.reports.database.MarkerRepo
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.LatLons
import de.westnordost.osmapi.map.data.OsmLatLon
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import retrofit2.Response
import java.io.IOException
import java.time.LocalDateTime
import kotlin.random.Random


@RunWith(AndroidJUnit4::class)
class VariousTesting {

    @UiThreadTest
    fun addPinQuest(){
        val loc = Location(Coordinates(0.0,0.0),"point")
        val newQuest = UzhQuest2(loc,0,false,"1111","pinTesting","",
            emptyList(),
            Tags(emptyList<Tag>()),"","", Verifiers(emptyList<Verifier>()), Histories(emptyList<History>()),LocalDateTime.now().toString(),LocalDateTime.now().toString(),
        "","","",
            listOf(0.0,0.0))
    }
}
