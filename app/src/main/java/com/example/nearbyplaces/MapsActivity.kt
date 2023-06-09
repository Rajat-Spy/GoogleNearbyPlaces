package com.example.nearbyplaces

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.nearbyplaces.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.util.Objects
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val Request_code = 101
    private lateinit var locationCallback: LocationCallback
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private val proximityRadius: Int = 10000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        hospital = findViewById(R.id.hospital)
//        restaurant = findViewById(R.id.restaurant)
//        parks = findViewById(R.id.parks)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.applicationContext)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    fun onClick(view: View){
        val hospital = "hospital"
        val restaurant = "restaurant"
        val parks = "park"
        var transferData = arrayOfNulls<Any>(2)
        lateinit var getNearbyPlaces: FetchData


        when(view.id){
            R.id.ic_magnify -> {
                val addressField: EditText = findViewById(R.id.location_search)
                val address: String  = addressField.text.toString()
                var addressList: List<Address>
                if(!TextUtils.isEmpty(address)){
                    val geoCoder = Geocoder(this)
                    addressList = geoCoder.getFromLocationName(address, 6) as List<Address>
                    if(addressList != null) {
                        for (i in addressList) {
                            var userAddress = i
                            val latLng = LatLng(userAddress.latitude, userAddress.longitude)
                            mMap.addMarker(MarkerOptions().position(latLng).title(address))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                        }
                    }
                }
            }
            R.id.hospital -> {
                mMap.clear()
                val url = getUrl(latitude, longitude, hospital)
                transferData[0] = mMap
                transferData[1] = url
                Log.d("MapsActivity", "transfer data = $transferData")
                var fetchData = FetchData()
                fetchData.execute(transferData)
                Toast.makeText(this, "Searching for nearby Hospitals...", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Showing nearby Hospitals...", Toast.LENGTH_SHORT).show()
            }
            R.id.restaurant -> {
                mMap.clear()
                val url = getUrl(latitude, longitude, restaurant)
                transferData[0] = mMap
                transferData[1] = url
                var fetchData = FetchData()
                fetchData.execute(transferData)
                Toast.makeText(this, "Searching for nearby Restaurants...", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Showing nearby Restaurants...", Toast.LENGTH_SHORT).show()
            }
            R.id.parks -> {
                mMap.clear()
                val url = getUrl(latitude, longitude, parks)
                transferData[0] = mMap
                transferData[1] = url
                var fetchData = FetchData()
                fetchData.execute(transferData)
                Toast.makeText(this, "Searching for nearby Museums...", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Showing nearby Museums...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Please Write any Location Name", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getUrl(latitude: Double, longitude: Double, nearbyPlace: String): String {
        val googleUrl = java.lang.StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googleUrl.append("location=$latitude,$longitude")
        googleUrl.append("&radius=$proximityRadius")
        googleUrl.append("&type=$nearbyPlace")
        googleUrl.append("&sensor=true")
        googleUrl.append("&key="+ resources.getString(R.string.YOUR_API_KEY))
        Log.d("MapsActivity", "url = $googleUrl")
        return googleUrl.toString()

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(Request_code) {
            Request_code -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getCurrentLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
       getCurrentLocation()
    }
    private fun getCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Request_code)
            return
        }
        var locationRequest = LocationRequest.create()
        locationRequest.setInterval(60000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(5000)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                Toast.makeText(applicationContext, "location result is = $p0", Toast.LENGTH_SHORT).show()

                if(p0 == null){
                    Toast.makeText(applicationContext, "curret location is Null", Toast.LENGTH_SHORT).show()
                    return
                }
                for(location in p0.locations){
                    if(location != null){
                        Toast.makeText(applicationContext, "curret location is  ${location.longitude}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
         fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        var task: Task<Location>  = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                val latLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(latLng).title("user Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
            }
        }

    }
}