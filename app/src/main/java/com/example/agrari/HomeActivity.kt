package com.example.agrari

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import com.example.agrari.databinding.ActivityHomeBinding
import com.example.agrari.fragments.ChatFragment
import com.example.agrari.fragments.HomeFragment
import com.example.agrari.fragments.SettingsFragment


class HomeActivity : AppCompatActivity() {


    lateinit var  binding: ActivityHomeBinding
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

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

        binding.navegationBar.setOnItemReselectedListener {
            when(it.itemId){
                R.id.homeIcon -> replaceFragment(HomeFragment())
                R.id.MensajesIcon -> replaceFragment(ChatFragment())
                R.id.settingsIcon -> replaceFragment(SettingsFragment())
            }
        }

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

    private  fun replaceFragment(fragment: Fragment){
        val fragmentManager= supportFragmentManager
        val fragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layoutNavigatorVendedor,fragment)
        fragmentTransaction.commit()
    }


}