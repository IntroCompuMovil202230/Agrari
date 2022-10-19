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
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

class ChooseTypeOfUserActivity : AppCompatActivity() {

    lateinit var  buyerButton: ImageButton
    lateinit var  sellerButton: ImageButton
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_type_of_user)
        buyerButton= findViewById(R.id.buyerButton)
        sellerButton= findViewById(R.id.sellerButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val value = sensorEvent.values[0]
                if (value < 10000) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }

        buyerButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
        })

        sellerButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,HomeVendedor::class.java))
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