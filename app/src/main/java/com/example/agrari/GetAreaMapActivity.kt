package com.example.agrari

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.agrari.databinding.ActivityGetAreaMapBinding
import com.example.mapstest.LocationController
import com.example.mapstest.MapController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task

class GetAreaMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityGetAreaMapBinding
    private lateinit var mapController: MapController
    private lateinit var locationController: LocationController
    lateinit var currentLocation: LatLng
    private var alreadyInitMapController: Boolean = false
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var locationCallback: LocationCallback
    lateinit var sensorManager: SensorManager
    lateinit var humiditySensor: Sensor
    lateinit var humiditySensorListener: SensorEventListener



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
        binding = ActivityGetAreaMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.locationController = LocationController(this, (getSystemService(LOCATION_SERVICE) as LocationManager))

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
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }


        //////////////////////////LIGHT SENSOR INIT////////////
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        humiditySensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                binding.humedadResultado.text="Humedad: ${event.values[0]}%"
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        binding.readRoutesButton.setOnClickListener {
            try {
                binding.metrajeResultado.text = "Metraje: ${this.mapController.paintMarkersPolygon().toInt()} m^2"

            }catch (e:Exception){
                binding.metrajeResultado.text = "Metraje: 0 m^2"
            }
        }
        sensorManager.registerListener(humiditySensorListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        if(!this.alreadyInitMapController){
            this.mapController= MapController(this,googleMap,this.currentLocation)
            this.locationController.startCurrentLocationUpdates(locationCallback)
            this.alreadyInitMapController=true
        }
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










}