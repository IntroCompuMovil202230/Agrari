package com.example.agrari.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.agrari.R
import com.example.agrari.databinding.ActivityCalcularRutaBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import java.io.IOException


class CalcularRutaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCalcularRutaBinding
    private lateinit var marcador: Marker
    var permission : String = Manifest.permission.ACCESS_FINE_LOCATION
    var permission_id: Int = 2
    lateinit var actual : LatLng
    lateinit var marcada : LatLng

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    //sensores
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null
    val REQUTEST_CHECK_SETTINGS = 3
    var is_gps_enabled = false

    var mGeocoder: Geocoder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCalcularRutaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mGeocoder = Geocoder(this)
        //sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
        lightSensorListener = lightSensorCode()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //inflate
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        locationCallback = createLocationCallback()

        requestPermission(this, permission, "Permiso para acceder al gps", permission_id)

        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.light_map))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16f))
        mMap.setOnMapLongClickListener { latLng ->
            val name: String = searchByLocation(latLng.latitude, latLng.longitude)
            if ("" != name) {
                if (marcador != null) {
                    marcador.remove()
                }
                marcador = mMap.addMarker(
                    MarkerOptions().position(latLng).title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )!!
                marcada = latLng
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                val distance: Double = SphericalUtil.computeDistanceBetween(actual, marcada)
                val dist = String.format("%.2f", distance / 1000)
                Toast.makeText(this, "Distancia: $dist km", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun lightSensorCode(): SensorEventListener? {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (mMap != null) {
                    if (event.values[0] < 5000) {
                        Log.i("MAPS", "DARK_MAP" + event.values[0])
                        mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle( this@CalcularRutaActivity,
                                R.raw.dark_map))
                    } else {
                        Log.i("MAPS", "LIGHT_MAP" + event.values[0])
                        mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this@CalcularRutaActivity,
                                R.raw.light_map))
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
    }

    private fun localizacionActual() {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED && is_gps_enabled
        ) {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener(
                this
            ) { location ->
                actual = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(actual).title("Posicion actual"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(actual))
            }
        }
    }

    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun createLocationCallback(): LocationCallback? {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    Log.i("tag", "location: $location")
                }
            }
        }
    }

    private fun requestPermission(
        context: Activity,
        permission: String,
        justification: String,
        idCode: Int
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, justification, Toast.LENGTH_LONG).show()
            }
            ActivityCompat.requestPermissions(context, arrayOf(permission), idCode)
        }
    }

    override fun onResume() {
        super.onResume()
        checkSettingsLocation()
        sensorManager!!.registerListener(
            lightSensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        sensorManager!!.unregisterListener(lightSensorListener)
    }

    private fun checkSettingsLocation() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            locationRequest!!
        )
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            is_gps_enabled = true
            startLocationUpdates()
            localizacionActual()
        }
        task.addOnFailureListener(this) { e ->
            val statusCode = (e as ApiException).statusCode
            is_gps_enabled = false
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED -> try {
                    val resolvable = e as ResolvableApiException
                    resolvable.startResolutionForResult(
                        this,
                        REQUTEST_CHECK_SETTINGS
                    )
                } catch (sendEx: SendIntentException) {
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (is_gps_enabled) {
                fusedLocationProviderClient!!.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback!!, Looper.getMainLooper()
                )
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUTEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                is_gps_enabled = true
                startLocationUpdates()
                localizacionActual()
            } else {
                Toast.makeText(this, "El gps no esta activado", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun searchByLocation(latitude: Double, longitude: Double): String {
        var addressName = ""
        try {
            val addresses = mGeocoder!!.getFromLocation(latitude, longitude, 2)
            if (addresses != null && !addresses.isEmpty()) {
                val addressResult = addresses[0]
                addressName = addressResult.getAddressLine(0)
                Log.i("MapsApp", addressResult.featureName)
            } else {
                Toast.makeText(this, "Direccion no encontrada", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }
}