package ch.uzh.ifi.accesscomplete.reports

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val rid: Int,
    /* @ColumnInfo(name = "report_type") val reportType: String?,
    @ColumnInfo(name = "location") val location: Location?,
    @ColumnInfo(name = "barrier_type") val barrierType: String?,
    @ColumnInfo(name = "path_available") val pathAvailable: String?,
    //@ColumnInfo(name = "bypass_available") val bypassAvailable: String?,
    @ColumnInfo(name = "path_width") val pathWidth: Double?,
    @ColumnInfo(name = "path_incline") val pathIncline: Double?,
    @ColumnInfo(name = "bypass_width") val bypassWidth: Double?,
    @ColumnInfo(name = "bypass_incline") val bypassIncline: Double?,
    @ColumnInfo(name = "barrier_width") val barrierWidth: Double?,
    @ColumnInfo(name = "barrier_height") val barrierHeight: Double?,
    @ColumnInfo(name = "wheelchair_accessible") val wheelchairAccessibility: String?,
    @ColumnInfo(name = "comment") val comment: String?,
    @ColumnInfo(name = "image_paths") val imagePaths: List<String>? */
    )
