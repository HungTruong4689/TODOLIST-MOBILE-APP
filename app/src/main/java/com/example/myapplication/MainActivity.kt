package com.example.myapplication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity(),LocationListener {
    lateinit var locationManager: LocationManager
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    //val url = "https://21wsp14pw.course.tamk.cloud/api/v1/task/list"
    private lateinit var myReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        myReceiver = MyReceiver()

    }

    //Location permission
    fun startPositioning(view: android.view.View) {
        // CHeck if we have permissions to follow user's location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //ok. We don't have the permission to use location. So let's aks user!
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )

            return
        }
        //register to folow the user's location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    override fun onLocationChanged(location: Location) {
        //Here i get the location event to uodate the GUI
        latitude = location.latitude
        longitude = location.longitude
        //Write them to textviews
        findViewById<TextView>(R.id.textLatitude).text =
            "Latitude: " + String.format("%.2f", latitude)
        findViewById<TextView>(R.id.textLongitude).text =
            "Longitude: " + String.format("%.2f", longitude)
    }
    //Post data to database
    fun addTodolist(view: android.view.View) {
        val url = "https://21wsp14pw.course.tamk.cloud/api/v1/task/list"
        val stringRequest = StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response -> handleResponse(response) },
            Response.ErrorListener { handleVolleyError() });
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
    private fun handleResponse(response: String) {
        //handle response here
        //Toast.makeText(this, response, Toast.LENGTH_LONG).show();

        // Parse the JSON response ( temperature, current condition and wind)
        val todoObject = JSONObject(response);
        //val List = todoObject.getJSONArray("task")
        val name = findViewById<EditText>(R.id.editTextName).text
        val tag = findViewById<EditText>(R.id.editTagName).text
        todoObject.put("name",name)
        todoObject.put("tag",tag)
        Toast.makeText(this, response, Toast.LENGTH_LONG).show();

    }

    private fun handleVolleyError() {
        //Error with request
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }
    //change to the second screen
    fun showList(view: android.view.View) {
        val intent = Intent(this, Todolist::class.java)
        startActivity(intent)



    }



    class MyReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);

            if(wifiState == WifiManager.WIFI_STATE_ENABLED){
                Toast.makeText(context, "Wifi is on", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Wifi is off", Toast.LENGTH_LONG).show();
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentfilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(myReceiver, intentfilter);
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(myReceiver);
    }
}
