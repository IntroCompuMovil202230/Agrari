package com.example.agrari

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.agrari.Model.AgrariPost
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.agrari.databinding.ActivityChoosePostLocationBinding
import com.example.agrari.services.AuthService
import com.example.agrari.services.DB_Service
import com.example.agrari.services.GeoCoderService
import com.example.mapstest.LocationController
import com.example.agrari.services.MapService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class ChoosePostLocationActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var binding: ActivityChoosePostLocationBinding
    private lateinit var locationController: LocationController
    private var mapService: MapService? = null
    private var counter: Int = 0
    private lateinit var authService: AuthService
    private lateinit var dbService: DB_Service
    private var alreadyCheck: Boolean = false
    lateinit var sensorManager: SensorManager
    lateinit var lightSensor: Sensor
    lateinit var lightSensorListener: SensorEventListener
    private lateinit var mapFragment: SupportMapFragment
    private  lateinit var geoCoderService: GeoCoderService
    lateinit var currentPost: AgrariPost

    var getLocationSettings: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.i("CHECKING LOCATION SETTINGS", "GPS is ON")
                if (requestAllLicationPermissions()) {
                    this.locationController.startLocationUpdates()
                }

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePostLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)


        authService = AuthService()
        dbService = DB_Service()
        geoCoderService= GeoCoderService(Geocoder(this))


        currentPost= intent.getSerializableExtra("newPost") as AgrariPost


        ////////////INIT LOCATION CONTROLLER////////////////////////////////////////////////
        this.locationController =
            LocationController(this, LocationServices.getFusedLocationProviderClient(this))
        this.locationController.locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                if (location != null) {
                    Log.i(
                        "LOCATION CALLBACK RESULT",
                        "${location.latitude} : ${location.longitude}"
                    )
                    if (mapService != null) {
                        if (counter <= 2) {
                            Log.i("COUNTER", "VALUE: $counter")
                            mapService!!.updateCurrentPositionMarket(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ), true, authService.getCurrentUser()
                            )
                            counter++

                        } else {
                            mapService!!.updateCurrentPositionMarket(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ), false, authService.getCurrentUser()
                            )
                        }
                    }
                }
            }
        }
        //////////////////////////////////////////////////////////////////////////////////////////


        //////////////////////////LIGHT SENSOR INIT////////////
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                changeMapLuminocity(event.values[0])
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        ////////////////////////////////////////////////////////////

        binding.chooseLocationButton.setOnClickListener {
            if(mapService!!.secondLocationMarket!=null){
                try {
                    this.currentPost.setPostLocationInfo(mapService!!.secondLocationMarket!!.position,geoCoderService.getDepartamentoFromLatLong(mapService!!.secondLocationMarket!!.position))
                    var intent = Intent(it.context,GetAreaMapActivity::class.java)
                    intent.putExtra("newPost",this.currentPost)
                    startActivity(intent)
                }catch (e:Exception){
                    println(e.toString())
                }
                //var intent= Intent(it.context,GetAreaMapActivity::class.java)
                //geoCoderService.getDepartamentoFromLatLong(mapService!!.secondLocationMarket!!.position)
                //startActivity(intent)
            }
        }



        mapFragment =
            supportFragmentManager.findFragmentById(com.example.agrari.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onResume() {
        super.onResume()
        if (!alreadyCheck) {
            ////////////CHECKING IF LOCATION IS ON AND CHECKING THE PERMISSIONS////////////////////////
            var task = this.locationController.checkLocationStatus()
            task.addOnSuccessListener {
                Log.i("CHECKING LOCATION SETTINGS", "GPS is ON")
                if (requestAllLicationPermissions()) {
                    this.locationController.startLocationUpdates()
                }
            }
            task.addOnFailureListener { e ->
                Log.i("CHECKING LOCATION SETTINGS", "GPS is OFF")
                if ((e as ApiException).statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                    val resolvable = ResolvableApiException(e.status)
                    val isr = IntentSenderRequest.Builder(resolvable.resolution).build()
                    getLocationSettings.launch(isr)
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////
            alreadyCheck = true
        }
    }

    override fun onPause() {
        super.onPause()
        this.locationController.stopLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (this.mapService == null) {
            this.mapService = MapService(this, googleMap,true)
        }
        sensorManager.registerListener(
            lightSensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    ////////PERMISSIONS/////////////////////////////////////////////////////
    @SuppressLint("MissingPermission", "SetTextI18n")
    fun requestAllLicationPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            requestPermissions(permissions, 2)
            return false
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("LOCATION-PERMISSIONS", "onRequestPermissionsResult CODE: ${requestCode}")
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.locationController.startLocationUpdates()
                }
            }
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun changeMapLuminocity(eventValue: Float) {
        this.mapService!!.changeMapLuminocity(eventValue)
        mapFragment.getMapAsync(this)
    }

}