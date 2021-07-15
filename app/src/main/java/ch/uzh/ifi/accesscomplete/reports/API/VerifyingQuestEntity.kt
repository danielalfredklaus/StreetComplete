package ch.uzh.ifi.accesscomplete.reports.API

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyingQuestEntity (
    val aid: String, //User ID from JWT, I dont know it, set server side tbh
    val markerid: String,
    val description: String, // User comment and values, pretty much everything goes in here
    var image_url: String

    )
