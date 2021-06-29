package ch.uzh.ifi.accesscomplete.reports.API

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyingQuestEntity (
    val aid: String, //User ID from JWT
    val markerid: String,
    val description: String, // User comment
    val image_url: String

    )
