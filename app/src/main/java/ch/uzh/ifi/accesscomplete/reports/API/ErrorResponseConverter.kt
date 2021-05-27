package ch.uzh.ifi.accesscomplete.reports.API

import android.util.Log
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.lang.Exception


class ErrorResponseConverter {
    val TAG = "ErrorResponseConverter"

    /**
     * Converts an ErrorBody to an actual ServerResponse Object
     * @param error response.errorBody
     * @return A ServerResponse, null if it fails
     * @author Daniel Alfred Klaus
     */
    fun ErrorBodyToServerResponse(error: ResponseBody?): ServerResponse? {
        var sr: ServerResponse? = null
        try {
            val jE = JSONObject(error!!.string())
            sr = ServerResponse(jE.optBoolean("success", false),jE.optString("email", ""),jE.optString("role",""),jE.optString("token",""),jE.optString("expiresIn",""),jE.optString("message",""),jE.optString("user",""))
        } catch(e: Exception){
            Log.e(TAG, "Failed to convert JSON to ServerResponse, $e")
        }
        return sr
    }

}
