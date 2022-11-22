package com.example.agrari

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import com.example.agrari.services.AuthService
import com.example.taller3_compu_movil.Notifications.NotificationData
import com.example.taller3_compu_movil.Notifications.PushNotification
import com.example.taller3_compu_movil.Notifications.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val TOPIC = "/topics/newPost"

class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var signUpText: TextView
    lateinit var imageView: ImageView
    lateinit var authService: AuthService
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askNotificationPermission()

        MyFirebaseMessagingService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        loginButton= findViewById(R.id.welcomeScreenLoginButton)
        signUpText= findViewById(R.id.welcomeScreenSignUpText)
        imageView = findViewById(R.id.imageView)
        authService= AuthService()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val value = sensorEvent.values[0]
                if (value < 10000) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                    imageView.setImageResource(R.drawable.whitelogo)
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                    imageView.setImageResource(R.drawable.logo)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }


        if(authService.getCurrentUser()!=null){
            if(authService.getCurrentUser()!!.displayName!!.contains("_AgrariJustUser")){
                startActivity(Intent(this,HomeActivity::class.java))
            }else{
                startActivity(Intent(this,HomeVendedor::class.java))
            }

        }

        loginButton.setOnClickListener(View.OnClickListener {view ->
            startActivity(Intent(this,LoginActivity::class.java))
        })

        signUpText.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        })
    }
    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            lightSensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(lightSensorListener)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i("NOTIFICATION-HANDLER", "Permission Granted")
        } else {
            Log.i("NOTIFICATION-HANDLER", "Permission NOT Granted")
        }
    }

    private fun askNotificationPermission() {
        Log.i("NOTIFICATION-HANDLER", "Ckecking permissions")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("NOTIFICATION-HANDLER", "Permission Granted")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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