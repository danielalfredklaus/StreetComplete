package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity
data class User(
    @PrimaryKey(autoGenerate = false) val email: String,
    @Json(name = "uname") val userName: String?,
    @Json(name = "fname") val firstName: String?,
    @Json(name = "lname") val lastName: String?,
    val password: String?
    )
