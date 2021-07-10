package ch.uzh.ifi.accesscomplete.reports.API

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageFile (
    @Json(name = "img_id")
    val imageID: String,

    @Json(name = "res_type")
    val resourceType: String,

    val timestamp: String,

    @Json(name = "img_name")
    val imageName: String,

    @Json(name = "img_url")
    val imageURL: String
)
