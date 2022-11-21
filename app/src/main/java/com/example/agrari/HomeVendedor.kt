package com.example.agrari

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import com.example.agrari.databinding.ActivityHomeVendedorBinding
import com.example.agrari.fragments.ChatFragment
import com.example.agrari.fragments.HomeVendedorFragment
import com.example.agrari.fragments.SettingsFragment
import com.example.agrari.fragments.UploadFragment

class HomeVendedor : AppCompatActivity() {

    lateinit var  binding: ActivityHomeVendedorBinding
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityHomeVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeVendedorFragment())

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

        binding.navegationBarVendedor.setOnItemReselectedListener {
            when(it.itemId){
                R.id.homeIconVendedor -> replaceFragment(HomeVendedorFragment())
                R.id.uploadIconVendedor -> replaceFragment(UploadFragment())
                R.id.MensajesIconVendedor -> replaceFragment(ChatFragment())
                R.id.settingsIconVendedor -> replaceFragment(SettingsFragment())
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