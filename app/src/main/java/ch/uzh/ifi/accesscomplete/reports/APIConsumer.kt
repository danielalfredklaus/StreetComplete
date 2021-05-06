package ch.uzh.ifi.accesscomplete.reports

import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class APIConsumer {

    val baseURL = ""
    lateinit var retrofit: Retrofit


    init {
         retrofit= Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    suspend fun login (password: String, email: String){

    }
}
