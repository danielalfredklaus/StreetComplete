package ch.uzh.ifi.accesscomplete.reports

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import ch.uzh.ifi.accesscomplete.reports.API.*
import ch.uzh.ifi.accesscomplete.reports.database.*
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import retrofit2.Response
import java.io.IOException
import java.time.LocalDateTime

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class MapMarkerRepoTest {
    private val TAG = "MarkerRepoTest"
    private lateinit var markerRepo: MarkerRepo
    private lateinit var markerDAO: MarkerDAO
    private lateinit var questDAO: UzhQuestDAO
    private lateinit var quest2DB: UzhQuest2DB
    private lateinit var db: MarkerDatabase
    private lateinit var webAccess: WebserverAccess
    private lateinit var sessionManager: SessionManager
    //val testUser: User = User("test@test.com", "test", "test", "test", "test")
    private val erc = ErrorResponseConverter()
    private var questMID = ""

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MarkerDatabase::class.java).build()
        quest2DB = Room.inMemoryDatabaseBuilder(
            context, UzhQuest2DB::class.java).build()
        markerDAO = db.markersDAO()
        sessionManager = SessionManager(context)
        webAccess = WebserverAccess()
        questDAO = quest2DB.uzhQuest2Dao()
        markerRepo = MarkerRepo(markerDAO, webAccess, questDAO)
        Log.d(TAG, "Finished @Before")
    }

    @Test
    fun atestLogin(){
        runBlocking {
            val loginData = LoginRequest("asdf@asdf.com","asdfasdf")
            val response: Response<ServerResponse> = markerRepo.login(loginData)
            if (response.isSuccessful){
                Log.d(TAG, "response seems successful")
                val body = response.body()
                Log.d(TAG, body.toString())
                assertTrue(body!!.success!!)
                assertEquals("asdf@asdf.com",body.email)
                Log.d(TAG, body.token!!)
                sessionManager.saveAuthToken(body.token!!)
            } else {
                val failedResponse = erc.ErrorBodyToServerResponse(response.errorBody())
                fail("Response was not as expected, message was ${failedResponse?.message}")
            }
        }
    }
    /* errorbody seems to be bugged in the dependency itself, i cant get it to work anymore */
    /*
    @Test
    fun failedLogin(){
        runBlocking {
            val loginData = LoginRequest("failed@login.com","purposelyFailingALoginForTesting")
            val response: Response<ServerResponse> = markerRepo.login(loginData)
            if(!response.isSuccessful){
                Log.d(TAG, "Login designed to fail has failed :SurprisedPikachuFace: ")
                var errrrr = response.errorBody()
                Log.d(TAG,errrrr!!.string())
                val failedR = erc.ErrorBodyToServerResponse(response.errorBody())
                assertFalse(failedR!!.success!!)
                Log.d(TAG, "Message received was: " + failedR.message!!)
            } else {
                fail("For some Reason the Login designed to fail was successful, wtf. There must be an account with this data...")
            }
        }
    } */

    @Test
    fun fetchAllMarkers(){
        runBlocking {
            try{
            val currentToken = sessionManager.fetchAuthToken()
            val response = markerRepo.getAllQuestsFromServer(currentToken!!) //Make sure to run the login beforehand at least once
            if(response.isSuccessful){
                Log.d(TAG, "Test Download complete")
                val list = response.body()!!
                list.forEach { Log.d(TAG, it.toString()) }
            } else {
                val failR = erc.ErrorBodyToServerResponse(response.errorBody())
                fail("Test failed because " + failR!!.message)
            }
            }catch (e: Exception){
                fail(e.message)
                e.printStackTrace()
                Log.e(TAG, e.toString())
            }
        }
    }
/*
    @Test
    fun postThenFetchMarker() {
        val currentToken = sessionManager.fetchAuthToken()!!
        val markerToAdd = Marker(); markerToAdd.latitude = 69; markerToAdd.longitude =
            69; markerToAdd.title = "nice"; markerToAdd.subtitle = "fromtestingCanbedeleted"
        markerToAdd.markerid = "696969"
        runBlocking {
            val firstResponse = markerRepo.postMarkerToServer(currentToken, markerToAdd)

            if (firstResponse.isSuccessful) {
                Log.d(TAG, "Successful Post was made")
                Log.d(TAG, firstResponse.body().toString())
            } else {
                Log.d(TAG, firstResponse.errorBody()!!.string())
                fail("Post has failed")
            }
            //val response = markerRepo.getAllMarkersFromServer(currentToken)

        }

        runBlocking {

            val secondResponse = markerRepo.getMarker(currentToken, "696969")
            if (secondResponse.isSuccessful) {
                Log.d(TAG, "Successful GET was made")
                Log.d(TAG, "Body is: " + secondResponse.body().toString())
                val fetchedMarker = secondResponse.body()
                assertEquals(markerToAdd.title, fetchedMarker?.title)
                assertEquals(markerToAdd.latitude, fetchedMarker?.latitude)
                assertEquals(markerToAdd.longitude, fetchedMarker?.longitude)
            } else {
                Log.d(TAG, secondResponse.errorBody()!!.string())$
                fail("GET has failed")
            }
        }
    }
*/
    /*
    @Test
    fun bpostMarker(){
        runBlocking {
            val currentToken = sessionManager.fetchAuthToken()!!
            val newMarker = MapMarker("point",16.0,6.0,"I don't like sand","It's coarse and rough and irritating", "and it gets everywhere", "I'm pickle riiick", "", 0,
                listOf(NoIdTag("width","55cm")) )
            val response = markerRepo.postMarkerToServer(currentToken,newMarker)
            if(response.isSuccessful){
                Log.d(TAG, response.body().toString())
                val newQuest: UzhQuest? = response.body()
                Log.d(TAG,"New Quest MID is" + newQuest?.mid)
                assertEquals("point", newQuest?.location?.geoType)
                assertEquals("I don't like sand", newQuest?.title)
                questMID = newQuest!!.mid
            }

        }
    }

    @Test
    fun cfetchMarker(){
        runBlocking{
            val currentToken = sessionManager.fetchAuthToken()!!
            val response = markerRepo.getQuestFromServer(currentToken, questMID) //REPLACE mID FOR TESTING IF NEEDED isy-SnnO-pRlEHFTSVr0uqp
            if (response.isSuccessful) {
                Log.d(TAG, "Successful GET was made")
                Log.d(TAG, "FetchMarker Body is: " + response.body().toString())
                val fetchedQuest = response.body()
                assertEquals("string", fetchedQuest?.title)
                assertEquals(listOf(0.0, 0.0), listOf(fetchedQuest?.location?.coordinates?.latitude,fetchedQuest?.location?.coordinates?.longitude))
                assertEquals("point", fetchedQuest?.location?.geoType)
            } else {
                Log.d(TAG, response.errorBody()!!.string())
                fail("GET has failed")
            }
        }
    }

    @Test
    fun deleteMarker(){
        runBlocking {
            val currentToken = sessionManager.fetchAuthToken()!!
            val response = markerRepo.removeQuestFromServer(currentToken,questMID)
            if(response.isSuccessful){
                assertEquals("Successfully Deleted Marker", response.body()?.message)
                Log.d(TAG, "Delete Marker Body: " + response.body().toString())
            } else {
                fail("Deletion failed")
            }
        }
    }

     */

    @Test
    fun eEditMarker(){
        runBlocking {
            val currentToken = sessionManager.fetchAuthToken()!!
            val response = markerRepo.getQuestFromServer(currentToken, "Nx25bzydkuTDhNFBMdQtZ") //
            if (response.isSuccessful) {
                Log.d(TAG, "GET successful")
                Log.d(TAG, "FetchMarker Body is: " + response.body().toString())
                val fetchedQuest = response.body()!!
                //fetchedQuest.verifierCount?.plus(1)
                //fetchedQuest.updatedon = LocalDateTime.now().toString()
                val response2 = markerRepo.updateMarkerOnServer(currentToken, VerifyingQuestEntity("notanaid",fetchedQuest.mid,"Tested the PUT function", ""))
                if(response2.isSuccessful){
                    Log.d(TAG, "Put success, Response was ${response2.body().toString()}")
                } else {
                    fail(response2.errorBody().toString())
                }


            } else {
                //Log.d(TAG, response.errorBody()!!.string())
                fail("GET has failed with message ${response.message()}")
            }
        }

    }



    /*
    @Test
    @Throws(IOException::class)
    fun register(){
        runBlocking {
            val registrationR = markerRepo.registerUser(testUser)
            if(registrationR.isSuccessful){
                val body = registrationR.body()
                assertNotNull(body)
                assertEquals("true",body!!.success)
                assertEquals("User Account Successfully Registered", body.message)
            } else {
                fail("Registration failed")
                Log.e(TAG,registrationR.errorBody().toString())
            }

        }
    } */
    /*
    @Test
    @Throws(Exception::class)
    fun registerUserandLogin() {
        try {
            val user: User = User("test@test.com", "test", "test", "test", "test")

            runBlocking {
                var registrationR = markerRepo.registerUser(user)
                var loginR: Response<ServerResponse>?
                var body: ServerResponse?
                var body2: ServerResponse?
                if (registrationR.isSuccessful) {
                    loginR = markerRepo.login(LoginRequest(user.email, user.password))
                    if (loginR.isSuccessful) {
                        body2 = loginR.body()
                        if (body2 != null) {
                            sessionManager.saveAuthToken(body2.token!!)
                            Log.d(TAG, "${sessionManager.fetchAuthToken()} was stored")
                        } else {
                            Log.d(TAG, "body2 is null")
                        }
                    } else Log.d(TAG, registrationR.errorBody()!!.string())

                } else {
                    Log.d("Test", registrationR.errorBody()!!.string())
                    //val jObjError = JSONObject(registrationR.errorBody()!!.string())
                    //jObjError.getJSONObject("error").getString("message")
                }

            }


            //val byName = userDao.findUsersByName("george")
            assertFalse(sessionManager.fetchAuthToken().isNullOrEmpty())
        } catch (e: Exception){
            Log.e(TAG, "Exception occured", e)
        }
    } */

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
