package ch.uzh.ifi.accesscomplete.reports.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Suppress("SpellCheckingInspection") //shut up with the id spellchecking
@JsonClass(generateAdapter = true)
@Entity
data class MapMarker(
    var geo_type: String?, //either point or polygon
    @Json(name = "lat") var latitude: Double?,
    @Json(name = "long") var longitude: Double?, //Kotlin allows long for a variable name, but Java doesn't, so when converting to a java class for JSON, it throws a stupid error
    var title: String?, // Quest title
    var subtitle: String?,  //Quest subtitle
    var description: String?,   //Quest description
    var comments: String?,
    var image_url: String?,//String that is a link to the uploaded image of the user
    var version: Int = 0,
    @Embedded
    @Json(name =  "tags") val tagsWithoutID: TagsWithoutID?,
    @PrimaryKey(autoGenerate = false) var nodeid: String = ""
    ){
    constructor() : this("",0.0,0.0,"","","","","",0,TagsWithoutID(mutableListOf<NoIdTag>())) //float 0.0f
}

data class TagsWithoutID(var tagListWithoutID: List<NoIdTag>)
data class NoIdTag(
    val k: String,
    var v: String
)

/*
aid	string

User ID from JWT

geo_type	string
Location type marker

Enum:
[ point, polygon ]
lat	integer
Latitude

long	integer
Longitude

title	string
Title of Quest

subtitle	string
Subtitle of the Quest

description	string
Description of the Quest

tags	[
example: List [ OrderedMap { "k": "Tag Name", "v": "Tag Value" }, OrderedMap { "k": "Tag Name", "v": "Tag Value" }, "..." ]
string
properties: OrderedMap { "id": OrderedMap { "type": "integer" }, "name": OrderedMap { "type": "string" } }]
image_url	string
*/




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
