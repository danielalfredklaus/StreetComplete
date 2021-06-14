package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class UzhQuest(
    val location: Location,

    @Json(name = "verifier_count")
    val verifierCount: Int,

    val isActive: Boolean,
    val mid: String,
    val title: String,
    val subtitle: String,

    @Json(name = "image_url")
    val imageURL: String,

    val tags: List<Tag>?,
    val description: String,
    val updatedby: String,
    val verifiers: List<Verifier>?,
    val history: List<History>?,
    val createdon: String,
    val updatedon: String,
    val changeset: String
)


data class History (
    @Json(name = "_id")
    val id: String,

    val verifierid: String,

    @Json(name = "image_url")
    val imageURL: String,

    val description: String,
    val createdon: String //as LocalDateTime Formatting
)

data class Location (
    val coordinates: List<String>,

    @Json(name = "geo_type")
    val geoType: String //point or polygon
)

data class Tag (
    @Json(name = "_id")
    val id: String,

    val k: String,
    var v: String
)

data class Verifier (
    @Json(name = "_id")
    val id: String?,

    val aid: String?
)


