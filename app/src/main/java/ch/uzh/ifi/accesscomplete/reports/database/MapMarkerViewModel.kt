package ch.uzh.ifi.accesscomplete.reports.database

import android.util.Log
import androidx.lifecycle.*
import ch.uzh.ifi.accesscomplete.reports.API.LoginRequest
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest2
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuestConverter
import kotlinx.coroutines.launch

class MapMarkerViewModel(private val repo: MarkerRepo): ViewModel() {

    /*TODO: User registers during an activity, email and password are saved in sharedprefs (private mode is important here) and handed in as LoginRequest
    maybe
     */
    val conv = UzhQuestConverter()
    var loginRequest: LoginRequest = LoginRequest("","")
    val TAG = "MapMarkerViewModel"
    private var currentKey = ""
    val isloggedIn: LiveData<Boolean> = liveData{
        val response = repo.login(loginRequest)
        if(response.isSuccessful) { currentKey= response.body()!!.token!!; emit(response.body()!!.success!!);Log.d(TAG, "Successful login, key is: $currentKey") }
        else { emit(false); Log.e(TAG, "Login failed") }
    }

    val allMapMarkers: LiveData<List<UzhQuest2>> = liveData{
        val data : LiveData<List<UzhQuest2>> = liveData {emit(repo.getAllQuestsFromDB())}
        emitSource(data)
        val response = repo.getAllQuestsFromServer(currentKey)
        if(response.isSuccessful){
            val retrievedQuests: List<UzhQuest> = response.body() ?: return@liveData
            for(q in retrievedQuests){
                val q2 = conv.convertToQuest2(q)
                if(repo.checkIfQuestExists(q.mid)) repo.updateQuests(q2)
                else repo.insertQuest(q2)
            }
        }
    }

    fun insertMarker(marker: MapMarker) = viewModelScope.launch{
        repo.insertMarker(marker)
        repo.postMarkerToServer(currentKey, marker)
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

class MapMarkerViewModelFactory(private val repo: MarkerRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapMarkerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapMarkerViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
