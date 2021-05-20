package ch.uzh.ifi.accesscomplete.reports

import ch.uzh.ifi.accesscomplete.reports.API.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

//Supposed to communicate with our local DB and the Webserver and be able to know which one to call when
//https://developer.android.com/codelabs/android-room-with-a-view-kotlin#8
class MarkerRepo (private val markerDAO: MarkerDAO, private val webserverAccess: WebserverAccess) {


    suspend fun registerUser(user: User): Response<ServerResponse> {
        return webserverAccess.mastersAPI.registerAsync(user).await()
    }

    suspend fun login(loginRequest: LoginRequest): Response<ServerResponse> {
        return webserverAccess.mastersAPI.loginAsync(loginRequest).await()
    }

    suspend fun insertIntoDB(marker: Marker){
        markerDAO.insertAll(marker)
    }

    suspend fun postMarkerToServer(token:String, marker: Marker){
        webserverAccess.mastersAPI.addMarkerAsync(token, marker).await()
    }

    fun getAllFromDB(): Flow<List<Marker>> {
        return markerDAO.getAll()
    }

    suspend fun getAllMarkersFromServer(token: String): Response<List<Marker>> {
        return webserverAccess.mastersAPI.getMarkersAsync(token).await()
    }





}
