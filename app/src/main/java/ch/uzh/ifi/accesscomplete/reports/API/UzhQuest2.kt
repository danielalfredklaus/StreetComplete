package ch.uzh.ifi.accesscomplete.reports.API

import android.annotation.SuppressLint
import android.os.Build
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementPointGeometry
import ch.uzh.ifi.accesscomplete.data.quest.Quest
import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import ch.uzh.ifi.accesscomplete.reports.UzhElementQuestType
import ch.uzh.ifi.accesscomplete.reports.VerifyReportQuestType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.westnordost.osmapi.map.data.LatLon
import de.westnordost.osmapi.map.data.OsmLatLon
import java.time.LocalDateTime
import java.util.*

@Entity
data class UzhQuest2(

    @Embedded
    val location: Location?,

    @Json(name = "verifier_count")
    val verifierCount: Int?,

    val isActive: Boolean?,
    @PrimaryKey val mid: String,
    val title: String?,
    val subtitle: String?,

    @Json(name = "image_url")
    val imageURL: List<String>?,

    @Embedded
    val tags: Tags?,
    val description: String?,
    val updatedby: String?,
    @Embedded
    val verifiers: Verifiers?,
    @Embedded
    val history: Histories?,
    val createdon: String?,
    val updatedon: String?,
    val changeset: String?,
    val version: String?,
    @Json(name = "nodeid") val nodeID: String?,
    @Json(name = "marker_location")
    val markerLocation: List<Double>?


    ):Quest {
    override var id: Long? = Random().nextLong()
    override val center: LatLon get()= OsmLatLon(location?.coordinates!!.latitude,location.coordinates.longitude)
    override val markerLocations get()= listOf(center)
    override val geometry: ElementGeometry get() = ElementPointGeometry(center)
    override val type: QuestType<*> get() = VerifyReportQuestType()
    override var status: QuestStatus = QuestStatus.NEW
    override val lastUpdate: Date get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val parsedLDT= LocalDateTime.parse(updatedon)
            Date(parsedLDT.year,parsedLDT.monthValue,parsedLDT.dayOfMonth,parsedLDT.hour,parsedLDT.minute,parsedLDT.second)
        } else {
            Date() //TODO: maybe parse date properly somehow
        }
    }

}

public class Tags(var tags: List<Tag>) {
}

public class Verifiers(var verifiers: List<Verifier>) {
}

public class Histories(var history: List<History>){
}
