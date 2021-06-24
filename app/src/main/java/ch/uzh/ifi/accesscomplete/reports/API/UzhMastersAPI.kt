package ch.uzh.ifi.accesscomplete.reports.API

import ch.uzh.ifi.accesscomplete.reports.database.MapMarker
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

//https://www.andreasjakl.com/how-to-retrofit-moshi-coroutines-recycler-view-for-rest-web-service-operations-with-kotlin-for-android/
//This is supposed to be the interface mapping the api from the webserver
interface uzhMastersAPI {

    @POST("login")
    suspend fun loginAsync(@Body loginRequest: LoginRequest): Response<ServerResponse>

    @POST("register")
    suspend fun registerAsync(@Body newUser: User):  Response<ServerResponse>

    @GET("apg/msrv/list")
    suspend fun getMarkersAsync(@Header("Authorization") token: String): Response<List<UzhQuest>>

    @POST("apg/msrv/marker")
    suspend fun addMarkerAsync(@Header("Authorization") token: String, @Body newMapMarker : MapMarker): Response<UzhQuest>

    @PUT("apg/msrv/marker")
    suspend fun updateMarkerAsync(@Header("Authorization") token: String, @Body newMapMarker: MapMarker) : Response<UzhQuest>

    @GET("apg/msrv/marker/{id}")
    suspend fun getMarkerAsync(@Header("Authorization") token: String, @Path("id") markerID: String) : Response<UzhQuest>

    @DELETE("apg/msrv/marker/{id}")
    suspend fun deleteMarkerAsync(@Header("Authorization") token: String, @Path("id") markerID: String) : Response<ServerResponse>


}
