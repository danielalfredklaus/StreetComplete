package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Suppress("SpellCheckingInspection") //shut up with the id spellchecking
@JsonClass(generateAdapter = true)
@Entity
data class Marker(
    @PrimaryKey(autoGenerate = true) val markerid: Int,
    var bb_type: String?,
    var geo_type: String?, //either point or polygon
    @Json(name = "lat") var latitude: Float?,
    @Json(name = "long") var longitude: Float?, //Kotlin allows long for a variable name, but Java doesn't, so when converting to a java class for JSON, it throws a stupid error
    var title: String?,
    var subtitle: String?,
    var description: String?,
    var verifiers: String?, //UID of the people who verified the quest
    var img_resid: String?, //String that is a link to the uploaded image of the user
    var verifier_count: Int? //counter for number of verifications
    )



//@ColumnInfo(name = "barrier_type") val barrierType: String?,
//@ColumnInfo(name = "path_available") val pathAvailable: String?,
//@ColumnInfo(name = "bypass_available") val bypassAvailable: String?,
//@ColumnInfo(name = "path_width") val pathWidth: Double?,
//@ColumnInfo(name = "path_incline") val pathIncline: Double?,
//@ColumnInfo(name = "bypass_width") val bypassWidth: Double?,
//@ColumnInfo(name = "bypass_incline") val bypassIncline: Double?,
//@ColumnInfo(name = "barrier_width") val barrierWidth: Double?,
//@ColumnInfo(name = "barrier_height") val barrierHeight: Double?,
//@ColumnInfo(name = "wheelchair_accessible") val wheelchairAccessibility: String?,
