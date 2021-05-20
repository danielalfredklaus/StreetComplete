package ch.uzh.ifi.accesscomplete.reports

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val rid: Int,
    val reportType: String?,
    val locationX: Float?,
    val locationY: Float?,
    val content: Map<String, String>?,
    val comment: String?,
    val imagePaths: List<String>?
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
