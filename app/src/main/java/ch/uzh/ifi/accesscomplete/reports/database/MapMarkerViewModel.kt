package ch.uzh.ifi.accesscomplete.reports.database

import android.util.Log
import androidx.lifecycle.*
import ch.uzh.ifi.accesscomplete.reports.API.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class MapMarkerViewModel(private val repo: MarkerRepo): ViewModel() {

    /*TODO: User registers during an activity, email and password are saved in sharedprefs (private mode is important here) and handed in as LoginRequest
    maybe
     */
    val registerUrl = "https://uzhmp-api-gateway-77xdzfzvua-ew.a.run.app/api/v2/register"
    val loginUrl = "https://uzhmp-api-gateway-77xdzfzvua-ew.a.run.app/api/v2/login"
    val conv = UzhQuestConverter()
    var loginRequest: LoginRequest = LoginRequest("", "")
    val TAG = "MapMarkerViewModel"
    private var currentKey = ""
    val triggerLogin: MutableLiveData<Boolean> =
        MutableLiveData(true) //True while user is not logged in, but actually not using this anyomre for anything
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
        } else if (loginResult.value != LoginState.SUCCESS) {
                val response = repo.login(loginUrl, loginRequest)
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
        if(imageList.value!!.isNotEmpty()){
            marker.image_url = imageList.value!!.first().imageURL
            imageList.value = mutableListOf()
        }
        repo.insertMarker(marker)
        val response = repo.postMarkerToServer(currentKey, marker)
        if(response.isSuccessful){
            val newQuest: UzhQuest = response.body() ?: return@launch
            val newQuest2: UzhQuest2 = conv.convertToQuest2(newQuest)
            repo.insertQuest(newQuest2)
            val tempList = allMapMarkers.value
            tempList?.add(newQuest2)
            allMapMarkers.postValue(tempList)
            Log.d(TAG, "Upload and insert successful")
        } else {
            runCatching { Log.e(TAG, "Post has failed with reason ${response.errorBody()?.string()}") }
        }
    }

    fun getQuestLocal(id: Long): UzhQuest2? {
        return allMapMarkers.value?.find{ id == it.id }
    }

    //Image Stuff

    var imageList: MutableLiveData<MutableList<ImageFile>> = MutableLiveData(mutableListOf())

    fun uploadImage(imgPath: String) = viewModelScope.launch {
        val file: File = File(imgPath)
        if(file.exists()){
            val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
            val body = MultipartBody.Part.createFormData("image", file.name, requestBody)
            val response = repo.uploadOneImageToServer(currentKey, body)
            if(response.isSuccessful){
                val imageFile: ImageFile = response.body()!!
                Log.d(TAG, "Image ID: "+ imageFile.imageID)
                val tempList = imageList.value
                tempList?.add(imageFile)
                imageList.postValue(tempList)
            } else {
                runCatching { Log.e(TAG, "Image upload failed, Reason: ${response.errorBody()?.string()}") }
            }
        }
    }

    fun verifyMarker(verif: VerifyingQuestEntity) = viewModelScope.launch {

    }

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
