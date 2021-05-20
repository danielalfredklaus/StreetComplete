package ch.uzh.ifi.accesscomplete.reports

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import ch.uzh.ifi.accesscomplete.reports.API.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Fail
import org.json.JSONObject
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import retrofit2.Response
import java.io.IOException
import java.lang.Exception


@RunWith(AndroidJUnit4::class)
class MarkerRepoTest {
    private val TAG = "MarkerRepoTest"
    private lateinit var markerRepo: MarkerRepo
    private lateinit var markerDAO: MarkerDAO
    private lateinit var db: MarkerDatabase
    private lateinit var webAccess: WebserverAccess
    private lateinit var sessionManager: SessionManager
    val testUser: User = User("test@test.com", "test", "test", "test", "test")

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MarkerDatabase::class.java).build()
        markerDAO = db.markersDAO()
        sessionManager = SessionManager(context)
        webAccess = WebserverAccess()
    }

    @Test
    fun test(){
        runBlocking {
            val loginData = LoginRequest("asdf@asdf.com","asdfasdf")
            val response = webAccess.mastersAPI.loginAsync(loginData).await()
            if (response.isSuccessful){
                Log.d(TAG, "response seems successful")
                val body = response.body()
                assertEquals("true",body!!.success)
                Log.d(TAG, body.token)
            } else {
                fail(response.errorBody().toString())
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
