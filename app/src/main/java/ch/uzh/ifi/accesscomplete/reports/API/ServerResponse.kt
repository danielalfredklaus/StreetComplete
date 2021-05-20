package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerResponse(
    var success: String? = "",
    var email: String? = "",
    var role: String? = "",
    var token: String? = "",
    var expiresIn: String? = "",
    //val message: String?,
    //val user: String?
)
