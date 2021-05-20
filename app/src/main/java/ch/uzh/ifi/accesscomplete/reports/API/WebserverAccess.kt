package ch.uzh.ifi.accesscomplete.reports.API

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//https://www.andreasjakl.com/how-to-retrofit-moshi-coroutines-recycler-view-for-rest-web-service-operations-with-kotlin-for-android/
class WebserverAccess {
    private val TAG = "WebserverAccess"
    private val url = "https://uzhmp-api-gateway-77xdzfzvua-ew.a.run.app"
    val mastersAPI: uzhMastersAPI by lazy {
        Log.d(TAG, "Creating retrofit client")
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return@lazy retrofit.create(uzhMastersAPI::class.java)
    }

}
