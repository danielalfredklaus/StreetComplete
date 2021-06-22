package ch.uzh.ifi.accesscomplete.reports.database

import ch.uzh.ifi.accesscomplete.reports.API.*
import retrofit2.Response

//Supposed to communicate with our local DB and the Webserver and be able to know which one to call when
//https://developer.android.com/codelabs/android-room-with-a-view-kotlin#8
class MarkerRepo (private val markerDAO: MarkerDAO, private val webserverAccess: WebserverAccess, private val uzhQuestDAO: UzhQuestDAO) {


    suspend fun registerUser(user: User): Response<ServerResponse> {
        return webserverAccess.mastersAPI.registerAsync(user)
    }

    suspend fun login(loginRequest: LoginRequest): Response<ServerResponse> {
        return webserverAccess.mastersAPI.loginAsync(loginRequest)
    }

    suspend fun insertMarker(mapMarker: MapMarker){
        markerDAO.insertAll(mapMarker)
    }

    suspend fun postMarkerToServer(token:String, mapMarker: MapMarker): Response<UzhQuest>{
        return webserverAccess.mastersAPI.addMarkerAsync(token, mapMarker)
    }

    suspend fun getAllMarkersFromDB(): List<MapMarker> {
        return markerDAO.getAll()
    }

    suspend fun getAllMarkersFromServer(token: String): Response<List<MapMarker>> {
        return webserverAccess.mastersAPI.getMarkersAsync(token)
    }

    suspend fun getQuestFromServer(token: String, mID: String): Response<UzhQuest>{
        return webserverAccess.mastersAPI.getMarkerAsync(token, mID)
    }

    suspend fun insertQuest(q: UzhQuest2){
        uzhQuestDAO.insertAll(q)
    }

    suspend fun findQuestByID(id: String): UzhQuest2{
        return uzhQuestDAO.findByID(id)
    }

    suspend fun removeQuest(q: UzhQuest2){
        uzhQuestDAO.delete(q)
    }

    suspend fun removeQuestFromServer(token: String, mID: String): Response<ServerResponse>{
        return webserverAccess.mastersAPI.deleteMarkerAsync(token, mID)
    }





}
