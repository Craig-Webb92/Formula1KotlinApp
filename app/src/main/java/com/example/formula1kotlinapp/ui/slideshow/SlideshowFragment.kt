package com.example.formula1kotlinapp.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.f1.Response.JSONResponse
import com.example.formula1kotlinapp.R
import com.example.formula1kotlinapp.databinding.FragmentSlideshowBinding
import com.google.gson.Gson
import org.json.JSONException
import java.net.URL

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val listViewArray = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDriver()
        var list: ListView = view.findViewById(R.id.listView)
        list.adapter = ArrayAdapter(activity as Context, android.R.layout.simple_list_item_1, listViewArray)
        ListHelper.getListViewSize(list)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getDriver() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        var drivers = arrayListOf<String>()
        StrictMode.setThreadPolicy(policy)
        try {
            var response = URL("http://ergast.com/api/f1/current/driverStandings.json").readText()
            var gson = Gson()

            var commentResponse = gson.fromJson(response, JSONResponse::class.java)

            var driverList = (commentResponse.mRData.standingsTable.standingsLists[0].driverStandings)
            for (driver in driverList){
                listViewArray.add(driver.driver.familyName)
            }

        } catch (e2: JSONException){
            e2.printStackTrace();
        }



    }
}