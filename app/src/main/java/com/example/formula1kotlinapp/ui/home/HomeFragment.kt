package com.example.formula1kotlinapp.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.f1.Response.JSONResponse
import com.example.formula1kotlinapp.R
import com.example.formula1kotlinapp.databinding.FragmentHomeBinding
import com.google.gson.Gson
import org.json.JSONException
import java.net.URL
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val handler = Handler()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update TextView every second
        handler.post(object : Runnable {
            override fun run() {

                var time = listOf<String>()
                var date = listOf<String>()
                var circuitName = ""
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                try {
                    var response = URL("http://ergast.com/api/f1/current/next.json").readText()
                    var gson = Gson()


                    var commentResponse = gson.fromJson(response, JSONResponse::class.java)

                    var raceTime = (commentResponse.mRData.raceTable.races[0].time)
                    var raceDate = (commentResponse.mRData.raceTable.races[0].date)

                    time = raceTime.split(":")
                    date = raceDate.split("-")


                    var circuit = (commentResponse.mRData.raceTable.races[0].circuit.circuitId)

                    circuitName = circuit.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }

                    println(circuitName)
                }
                catch (e2: JSONException)
                {
                    //     println("Exception Thrown?")
                    e2.printStackTrace();
                }

                //  circuitText.text = "$circuitName"

                // Keep the postDelayed before the updateTime(), so when the event ends, the handler will stop too.
                handler.postDelayed(this, 1000)
                updateTime(time, date, view)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateTime(time: List<String>, date: List<String>, view: View) {

        val countdownText: TextView = view.findViewById(R.id.countdown_text)
        // Set Current Date
        val currentDate = Calendar.getInstance()
        // println(currentDate)
        // Set Event Date
        val eventDate = Calendar.getInstance()
        eventDate[Calendar.YEAR] = date[0].toInt()
        eventDate[Calendar.MONTH] = date[1].toInt() - 1 // 0-11 so 1 less
        eventDate[Calendar.DAY_OF_MONTH] = date[2].toInt()
        eventDate[Calendar.HOUR_OF_DAY] = time[0].toInt() - 1 // 0-23
        eventDate[Calendar.MINUTE] = time[1].toInt()
        eventDate[Calendar.SECOND] = 0
        eventDate.timeZone = TimeZone.getTimeZone("GMT")
        var timeZone = TimeZone.getDefault()
        var timeZoneOffset = timeZone.getOffset(eventDate.timeInMillis)

        //  println("HELP: " + eventDate)
        // Find how many milliseconds until the event
        val diff = (eventDate.timeInMillis + timeZoneOffset) - currentDate.timeInMillis
        //   println("diff: " + diff)
        // Change the milliseconds to days, hours, minutes and seconds
        val days = (diff / (24 * 60 * 60 * 1000))
        val hours = (diff / (1000 * 60 * 60) % 24)
        val minutes = (diff / (1000 * 60) % 60)
        val seconds = ((diff / 1000) % 60)
        // Display Countdown
        countdownText.text = "${days}d ${hours}h ${minutes}m ${seconds}s"
        // Show different text when the event has passed
        endEvent(currentDate, eventDate, view)
    }

    private fun endEvent(currentDate: Calendar, eventDate: Calendar, view: View) {
        val countdownText: TextView = view.findViewById(R.id.countdown_text)
        if (currentDate.time >= eventDate.time) {
            countdownText.text = "RACE DAY!"
            //Stop Handler
            handler.removeMessages(0)
        }
    }
}