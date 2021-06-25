package ch.uzh.ifi.accesscomplete.reports.API

import android.util.Log
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


//https://www.andreasjakl.com/how-to-retrofit-moshi-coroutines-recycler-view-for-rest-web-service-operations-with-kotlin-for-android/
class WebserverAccess {
    private val TAG = "WebserverAccess"
    private val url = "https://uzhmp-api-gateway-77xdzfzvua-ew.a.run.app/api/v1/"
    val mastersAPI: uzhMastersAPI by lazy {
        Log.d(TAG, "Creating retrofit client")
        /*
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor).build()  //The interceptor was only used for debugging */

        val moshi = Moshi.Builder()
            .add(MoshiDateAdapter())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            //.addCallAdapterFactory(CoroutineCallAdapterFactory()) not needed, just make everything a friggin suspend function, no adapter required anymore
            .build()

        return@lazy retrofit.create(uzhMastersAPI::class.java)
    }

}
