package com.example.agrari
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.example.agrari.databinding.ActivityGetAreaMapBinding
import com.example.agrari.services.AuthService
import com.example.agrari.services.DB_Service
import com.example.mapstest.LocationController
import com.example.agrari.services.MapService
import com.example.taller3_compu_movil.Notifications.NotificationData
import com.example.taller3_compu_movil.Notifications.PushNotification
import com.example.taller3_compu_movil.Notifications.RetrofitInstance
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetAreaMapActivity : AppCompatActivity(), OnMapReadyCallback {



    private lateinit var binding: ActivityGetAreaMapBinding
    private lateinit var locationController: LocationController
    private  var mapService: MapService?=null
    private  var counter:Int=0
    private lateinit var authService: AuthService
    private  lateinit var dbService: DB_Service
    private  var alreadyCheck: Boolean=false
    lateinit var sensorManager: SensorManager
    lateinit var lightSensor: Sensor
    lateinit var lightSensorListener: SensorEventListener
    private lateinit var mapFragment: SupportMapFragment
    lateinit var currentPost: AgrariPost
    lateinit var humiditySensor: Sensor
    lateinit var humiditySensorListener: SensorEventListener
    lateinit var temperatureSensor: Sensor
    lateinit var temperatureSensorListener: SensorEventListener
    var humidityValue:Float=0F
    var areaValue:Float=0F
    var temperatureValue:Float=0F

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
        binding = ActivityGetAreaMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)


        authService = AuthService()
        dbService= DB_Service()


        currentPost= intent.getSerializableExtra("newPost") as AgrariPost

        ////////////INIT LOCATION CONTROLLER////////////////////////////////////////////////
        this.locationController= LocationController(this, LocationServices.getFusedLocationProviderClient(this))
        this.locationController.locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                if (location!=null){
                    Log.i("LOCATION CALLBACK RESULT", "${location.latitude} : ${location.longitude}")
                    if(mapService!=null){
                        if(counter<=2){
                            Log.i("COUNTER", "VALUE: $counter")
                            mapService!!.updateCurrentPositionMarket(LatLng(location.latitude,location.longitude),true, authService.getCurrentUser())
                            counter++

                        }else{
                            mapService!!.updateCurrentPositionMarket(LatLng(location.latitude,location.longitude),false,authService.getCurrentUser())
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


        //////////////////////////HUMIDITY SENSOR INIT////////////
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        humiditySensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                humidityValue=String.format("%.1f", event.values[0]).toFloat()
                binding.humedadResultado.text="Humedad: "+humidityValue+" %"
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        ////////////////////////////////////////////////////////////


        //////////////////////////TEMPERATURE SENSOR INIT////////////
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        temperatureSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                temperatureValue=String.format("%.1f", event.values[0]).toFloat()
                binding.temperatureText.text="Temperatura: "+temperatureValue+" °C"
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        ////////////////////////////////////////////////////////////


        binding.readRoutesButton.setOnClickListener {
            try {
                this.areaValue=this.mapService!!.paintMarkersPolygon().toInt().toFloat()
                binding.metrajeResultado.text = "Metraje: ${areaValue} m^2"

            }catch (e:Exception){
                binding.metrajeResultado.text = "Metraje: 0 m^2"
            }
        }

        mapFragment = supportFragmentManager.findFragmentById(com.example.agrari.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.finalizarSubidoPost.setOnClickListener {view->
            if(areaValue>0F){
                this.currentPost.setAreaHumidityTemperature(areaValue,humidityValue,temperatureValue)
                this.dbService.addNewPost(this.currentPost)
                val title = "Nuevo post"
                val message = "Mirálo y conoce tu terreno ideal."
                if(title.isNotEmpty() && message.isNotEmpty()) {
                    PushNotification(
                        NotificationData(title, message),
                        TOPIC
                    ).also {
                        sendNotification(it)
                    }
                    var intent=Intent(view.context,MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(!alreadyCheck){
            ////////////CHECKING IF LOCATION IS ON AND CHECKING THE PERMISSIONS////////////////////////
            var task= this.locationController.checkLocationStatus()
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
            alreadyCheck=true
        }
    }

    override fun onPause() {
        super.onPause()
        this.locationController.stopLocationUpdates()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        if(this.mapService==null){
            this.mapService= MapService(this,googleMap,false)
        }
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(humiditySensorListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(temperatureSensorListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }


    ////////PERMISSIONS/////////////////////////////////////////////////////
    @SuppressLint("MissingPermission", "SetTextI18n")
    fun requestAllLicationPermissions():Boolean{
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("LOCATION-PERMISSIONS", "onRequestPermissionsResult CODE: ${requestCode}")
        when(requestCode){
            2-> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.locationController.startLocationUpdates()
                }
            }
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun changeMapLuminocity(eventValue: Float){
        this.mapService!!.changeMapLuminocity(eventValue)
        mapFragment.getMapAsync(this)
    }


    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            println(response.message())
            println(response.toString())
            if(response.isSuccessful) {
                Log.d("NOTIFICATION-DATA", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("RESPONSE-NOT-SUCCESFULL", response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e("ERROR-NOTIFICATION", e.toString())
        }
    }

}