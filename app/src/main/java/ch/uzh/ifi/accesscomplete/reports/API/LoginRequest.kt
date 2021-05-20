package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest (
    val email: String,
    val password: String?
    )
