package ch.uzh.ifi.accesscomplete.reports.database

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import ch.uzh.ifi.accesscomplete.reports.API.LoginRequest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.util.*

class MapMarkerViewModel(private val repo: MarkerRepo, private val loginRequest: LoginRequest): ViewModel() {

    /*TODO: User registers during an activity, email and password are saved in sharedprefs (private mode is important here) and handed in as LoginRequest
    maybe
     */

    val TAG = "MapMarkerViewModel"
    private var currentKey = ""
    val isloggedIn: LiveData<Boolean> = liveData{
        val response = repo.login(loginRequest)
        if(response.isSuccessful) { currentKey= response.body()!!.token!!; emit(response.body()!!.success!!);Log.d(TAG, "Successful login, key is: $currentKey") }
        else { emit(false); Log.e(TAG, "Login failed") }
    }

    val allMapMarkers: LiveData<List<MapMarker>> = liveData{
        val data = repo.getAllFromDB()
        emit(data)
    }



    /*
    val allWords: LiveData<List<Word>> = repository.allWords.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }
     */


}

class MapMarkerViewModelFactory(private val repo: MarkerRepo, private val loginRequest: LoginRequest) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapMarkerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapMarkerViewModel(repo, loginRequest) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
