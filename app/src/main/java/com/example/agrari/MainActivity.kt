package com.example.agrari

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.agrari.controller.AuthController
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var signUpText: TextView
    lateinit var imageView: ImageView
    lateinit var authController: AuthController
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginButton= findViewById(R.id.welcomeScreenLoginButton)
        signUpText= findViewById(R.id.welcomeScreenSignUpText)
        imageView = findViewById(R.id.imageView)
        authController=AuthController()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val value = sensorEvent.values[0]
                if (value < 10000) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                    imageView.setImageResource(R.drawable.logo2)
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                    imageView.setImageResource(R.drawable.logo)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }


        if(authController.getCurrentUser()!=null){
            //startActivity(Intent(this,HomeActivity::class.java))
            startActivity(Intent(this,HomeVendedor::class.java))
        }

        loginButton.setOnClickListener(View.OnClickListener {
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
}