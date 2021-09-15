package com.example.formula1kotlinapp.ui.gallery

import android.os.StrictMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.f1.Response.JSONResponse
import com.google.gson.Gson
import org.json.JSONException
import java.net.URL

class GalleryViewModel : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        val drivers = getDriver()
        println("Here " + drivers[0])
        value = "${drivers[0]}"

    }
    val text: LiveData<String> = _text


    fun getDriver():ArrayList<String> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        var drivers = arrayListOf<String>()
        StrictMode.setThreadPolicy(policy)
        try {
            var response = URL("http://ergast.com/api/f1/current/driverStandings.json").readText()
            var gson = Gson()

            var commentResponse = gson.fromJson(response, JSONResponse::class.java)

            var driverList = (commentResponse.mRData.standingsTable.standingsLists[0].driverStandings)
            for (driver in driverList){
                drivers.add(driver.driver.familyName)
            }

        } catch (e2: JSONException){
            e2.printStackTrace();
        }
        return drivers


    }

}