package ch.uzh.ifi.accesscomplete.reports.database

import android.util.Log
import androidx.lifecycle.*
import ch.uzh.ifi.accesscomplete.reports.API.LoginRequest
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest2
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuestConverter
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapMarkerViewModel(private val repo: MarkerRepo): ViewModel() {

    /*TODO: User registers during an activity, email and password are saved in sharedprefs (private mode is important here) and handed in as LoginRequest
    maybe
     */
    val conv = UzhQuestConverter()
    var loginRequest: LoginRequest = LoginRequest("", "")
    val TAG = "MapMarkerViewModel"
    private var currentKey = ""
    val triggerLogin: MutableLiveData<Boolean> =
        MutableLiveData(true) //True while user is not logged in
/*
    val isLoggedIn: LiveData<Boolean> = triggerLogin.switchMap { state ->
        liveData {
            if (triggerLogin.value!! && (loginRequest.email == "" || loginRequest.password == "")) {
                triggerLogin.value = true
                Log.d(TAG, "Login credentials empty")
                emit(false)
            } else while (triggerLogin.value!!) {
                val response = repo.login(loginRequest)
                if (response.isSuccessful) {
                    currentKey = response.body()!!.token!!
                    Log.d(TAG, "Login Token saved with length of ${currentKey.length}")
                    emit(response.body()?.success ?: true)
                    Log.d(TAG, "Successful login, key is: $currentKey")
                    triggerLogin.value = false
                } else {
                    emit(false);
                    Log.e(TAG, "Login failed")
                    triggerLogin.value = true
                }
                delay(10000)
                // note that `while(true)` is fine because the `delay(30_000)` below will cooperate in
                // cancellation if LiveData is not actively observed anymore
            }
            Log.d(TAG, "End of isLoggedIn fun")
        }
    } */

    val loginResult = MutableLiveData(LoginState.NOTINITIATED)
    fun doTheFuckingLogin() = viewModelScope.launch{
        if(loginRequest.email == ""){
            triggerLogin.value = true
            Log.d(TAG, "Email empty")
            loginResult.postValue(LoginState.NOEMAIL)

        } else if (loginRequest.password == "") {
            triggerLogin.value = true
            Log.d(TAG, "Password empty")
            loginResult.postValue(LoginState.NOPASSWORD)

        } else if(loginResult.value == LoginState.FAILED){
            Log.d(TAG, "Login has failed")
        } else if (loginResult.value != LoginState.SUCCESS) {
                val response = repo.login(loginRequest)
                if (response.isSuccessful) {
                    currentKey = response.body()!!.token!!
                    Log.d(TAG, "Login Token saved with length of ${currentKey.length}")
                    loginResult.postValue(LoginState.SUCCESS)
                    Log.d(TAG, "Successful login, key is: $currentKey")
                    triggerLogin.value = false
                    fillMarkerListFromServer()
                } else {
                    loginResult.postValue(LoginState.FAILED)
                    Log.e(TAG, "Login failed with reason: ${response.errorBody()?.string()}")
                    triggerLogin.value = true
                }
                // note that `while(true)` is fine because the `delay(30_000)` below will cooperate in
                // cancellation if LiveData is not actively observed anymore
            }
            Log.d(TAG, "End of fucking Login fun")

    }


    val allMapMarkers: MutableLiveData<MutableList<UzhQuest2>> = MutableLiveData(mutableListOf())


    fun fillMarkerListFromServer() = viewModelScope.launch{

        Log.d(TAG, "Starting to fetch Quests from the Server")
        while (loginResult.value != LoginState.SUCCESS) {
            delay(5000)
        }
        while (loginResult.value == LoginState.SUCCESS) {
            val response = repo.getAllOpenQuestsFromServer(currentKey)
            if (response.isSuccessful) {
                Log.d(TAG, "Successful response during quest fetching")
                val retrievedQuests: List<UzhQuest> = response.body() ?: return@launch
                for (q in retrievedQuests) {
                    val q2 = conv.convertToQuest2(q)
                    if (repo.checkIfQuestExists(q.mid)) repo.updateQuests(q2)
                    else repo.insertQuest(q2)
                }
            } else {
                Log.d(TAG, "Fetching Quests Failed, Reason: " + response.errorBody()?.string())
                if(response.message().contains("401")) loginResult.postValue(LoginState.FAILED)
            }
            val data: MutableList<UzhQuest2> = repo.getAllQuestsFromDB() as MutableList
            allMapMarkers.postValue(data)
            Log.d(TAG, "Finished fetching Quests from Server")
            delay(120000)
        }
    }


    fun insertMarker(marker: MapMarker) = viewModelScope.launch{
        repo.insertMarker(marker)
        val response = repo.postMarkerToServer(currentKey, marker)
        if(response.isSuccessful){
            val newQuest: UzhQuest = response.body() ?: return@launch
            val newQuest2: UzhQuest2 = conv.convertToQuest2(newQuest)
            repo.insertQuest(newQuest2)
            fillMarkerListFromServer()
            Log.d(TAG, "Upload and insert successful")
        } else {
            Log.e(TAG, "Post has failed with reason ${response.errorBody()?.string()}")
        }
    }

    fun getQuestLocal(id: Long): UzhQuest2? {
        return allMapMarkers.value?.find{ id == it.id }
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
