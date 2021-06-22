package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.Embedded
import androidx.room.PrimaryKey
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometry
import ch.uzh.ifi.accesscomplete.data.quest.Quest
import ch.uzh.ifi.accesscomplete.data.quest.QuestStatus
import ch.uzh.ifi.accesscomplete.data.quest.QuestType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.westnordost.osmapi.map.data.LatLon
import java.time.LocalDateTime
import java.util.*

@JsonClass(generateAdapter = true)
data class UzhQuest(


    val location: Location?,

    @Json(name = "verifier_count")
    val verifierCount: Int?,

    val isActive: Boolean?,
    val mid: String,
    val title: String?,
    val subtitle: String?,

    @Json(name = "image_url")
    val imageURL: List<String>?,

    val tags: List<Tag>? = emptyList(),
    val description: String?,
    val updatedby: String?,
    val verifiers: List<Verifier>? = emptyList(),
    val history: List<History>? = emptyList(),
    val createdon: String?,
    val updatedon: String?,
    val changeset: String?,
    val version: String?,
    @Json(name = "nodeid")
    val nodeID: String?,
    @Json(name = "marker_location")
    val markerLocation: List<Double>?

)

@JsonClass(generateAdapter = true)
data class History (
    @Json(name = "_id")
    val id: String,

    val verifierid: String,

    @Json(name = "image_url")
    val imageURL: String,

    val description: String,
    val createdon: String //as LocalDateTime Formatting
)

@JsonClass(generateAdapter = true)
data class Location (
    @Embedded
    val coordinates: Coordinates,

    @Json(name = "geo_type")
    val geoType: String //point or polygon
)

@JsonClass(generateAdapter = true)
data class Coordinates (
    @Json(name = "lat")
    val latitude: Double,
    @Json(name = "long")
    val longitude: Double
)

@JsonClass(generateAdapter = true)
data class Tag (
    @Json(name = "_id")
    val id: String,

    val k: String,
    var v: String
)

@JsonClass(generateAdapter = true)
data class Verifier (
    @Json(name = "_id")
    val id: String?,

    val aid: String?
)


