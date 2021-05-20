package ch.uzh.ifi.accesscomplete.reports.API

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

//https://www.andreasjakl.com/how-to-retrofit-moshi-coroutines-recycler-view-for-rest-web-service-operations-with-kotlin-for-android/
//This is supposed to be the interface mapping the api from the webserver
interface uzhMastersAPI {

    @POST("/login")
    fun loginAsync(@Body loginRequest: LoginRequest): Deferred<Response<ServerResponse>>
    /*
    @POST("/register")
    fun registerAsync(@Body newUser: User):  Deferred<Response<ServerResponse>>

    @GET("/list")
    fun getMarkersAsync(@Header("Authorization") token: String): Deferred<Response<List<Marker>>>

    @POST("/marker")
    fun addMarkerAsync(@Header("Authorization") token: String, @Body newMarker : Marker): Deferred<Response<Void>>

    @PUT("/marker")
    fun updateMarkerAsync(@Header("Authorization") token: String, @Body newMarker: Marker) : Deferred<Response<Void>>

    @GET("/marker/{id}")
    fun getMarkerAsync(@Header("Authorization") token: String, @Path("id") markerID: Int) : Deferred<Response<Marker>>

    @DELETE("/marker/{id}")
    fun deleteMarkerAsync(@Header("Authorization") token: String, @Path("id") markerID: Int) : Deferred<Response<Void>>
     */

}
