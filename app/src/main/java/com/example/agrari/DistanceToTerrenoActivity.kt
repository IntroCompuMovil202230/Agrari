package com.example.agrari
import android.Manifest
import android.annotation.SuppressLint
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
import com.example.agrari.databinding.ActivityDistanceToTerrenoBinding
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


class DistanceToTerrenoActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var secondLocation: LatLng
    private lateinit var binding: ActivityDistanceToTerrenoBinding
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
    private lateinit var geoCoderService: GeoCoderService
    private lateinit var publicacion:AgrariPost


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
        binding = ActivityDistanceToTerrenoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)


        authService = AuthService()
        dbService = DB_Service()
        geoCoderService = GeoCoderService(Geocoder(this))


        publicacion= intent.getSerializableExtra("post") as AgrariPost

        this.secondLocation= LatLng(publicacion.latitude.toDouble(),publicacion.longitude.toDouble())

        Log.i("DISTANCE-SCREEN","$secondLocation")


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
            this.mapService = MapService(this, googleMap, null)
            this.mapService!!.addMJustOnearker(this.secondLocation,this.publicacion.title)

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

/*
* class DistanceToTerrenoActivity : AppCompatActivity(), OnMapReadyCallback {




    private lateinit var binding: ActivityDistanceToTerrenoBinding
    private lateinit var mapController: MapService
    private lateinit var locationController: LocationController
    lateinit var currentLocation: LatLng
    private var alreadyInitMapController: Boolean = false
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var locationCallback: LocationCallback
    private lateinit var secondLocation:LatLng
    lateinit var sensorManager: SensorManager
    lateinit var lightSensor: Sensor
    lateinit var lightSensorListener: SensorEventListener



    var getLocationSettings: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                if (requestAllPermissions()) {
                    Log.i("LOCATION-PERMISSIONS", "ALL PERMISSIONS ACCEPTED")
                    this.locationController.getCurrentLocation().addOnCompleteListener(this) {
                        setCurrentLocation(it)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDistanceToTerrenoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)


        var publicacion= intent.getSerializableExtra("currentPublicacion") as Publicacion

        this.secondLocation= LatLng(publicacion.latitud,publicacion.longitud)

        Log.i("DISTANCE-SCREEN","$secondLocation")

        this.locationController =
            LocationController(this, (getSystemService(LOCATION_SERVICE) as LocationManager))

        this.locationController.checkLocationSettings().addOnSuccessListener {
            this.locationController.getCurrentLocation().addOnCompleteListener(this) {
                if (requestAllPermissions()) {
                    Log.i("LOCATION-PERMISSIONS", "ALL PERMISSIONS ACCEPTED")
                    this.locationController.getCurrentLocation().addOnCompleteListener(this) {
                        setCurrentLocation(it)
                    }
                }
            }
        }
        this.locationController.checkLocationSettings().addOnFailureListener {
            if ((it as ApiException).statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                val resolvable = it as ResolvableApiException
                val isr = IntentSenderRequest.Builder(resolvable.resolution).build()
                getLocationSettings.launch(isr)
            }
        }


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


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    updateCurrentPositionMarker()
                }
            }
        }

    }




    override fun onMapReady(googleMap: GoogleMap) {
        if(!this.alreadyInitMapController){
            this.mapController= MapService(this,googleMap,this.currentLocation)
            this.locationController.startCurrentLocationUpdates(locationCallback)
            this.alreadyInitMapController=true
            this.mapController.addMarker(this.secondLocation)
            this.mapController.drawRoute(this.secondLocation)
        }
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun setCurrentLocation(task: Task<Location>){
        val location: Location? = task.result
        if(location!=null) {
            Log.i("LOCATION-CONTROLLER", "READING LOCATION LATITUDE:${location.latitude}")
            Log.i("LOCATION-CONTROLLER", "READING LOCATION LONGITUDE:${location.longitude}")
            this.currentLocation= LatLng(location.latitude,location.longitude)
            this.mapFragment.getMapAsync(this)
        }else{
            Log.i("LOCATION", "LOCATION IS NULL: ${location}")
        }

    }

    private fun updateCurrentPositionMarker(){
        this.mapController.updateCurrentPositionMarket(this.currentLocation)
    }


    ////////PERMISSIONS/////////////////////////////////////////////////////

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun requestAllPermissions():Boolean{
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
        }else{
            requestPermissions(permissions, 2)
            return false
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("LOCATION-PERMISSIONS", "onRequestPermissionsResult CODE: ${requestCode}")
        when(requestCode){
            2-> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.locationController.getCurrentLocation().addOnCompleteListener(this){
                        setCurrentLocation(it)
                    }
                }
            }

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    fun changeMapLuminocity(eventValue: Float){
        this.mapController.changeMapLuminocity(eventValue)
        this.mapFragment.getMapAsync(this)
    }


}
*
*
* */