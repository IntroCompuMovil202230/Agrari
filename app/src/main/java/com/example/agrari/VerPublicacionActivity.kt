package com.example.agrari

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.agrari.databinding.ActivityVerPublicacionBinding
import com.google.android.gms.maps.model.LatLng
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.agrari.Model.AgrariPost
import com.example.agrari.services.AuthService
import com.example.taller3_compu_movil.controller.ImageEncodingController
import com.squareup.picasso.Picasso

class VerPublicacionActivity : AppCompatActivity() {
    lateinit var publicacion: AgrariPost
    private lateinit var binding: ActivityVerPublicacionBinding
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null
    lateinit var imageEncodingController: ImageEncodingController
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerPublicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        publicacion= intent.getSerializableExtra("post") as AgrariPost
        authService= AuthService()
        imageEncodingController= ImageEncodingController()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)


        binding.infoPublicacionTitulo.text= publicacion.title
        binding.infoPublicacionUbicacion.text= publicacion.departamento
        binding.infoPublicacionPrecio.text= " $ "+publicacion.price
        binding.infoPublicacionImagen.setImageBitmap(imageEncodingController.decodeImage(publicacion.image))


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



        binding.verDistanciaMapaButton.setOnClickListener {
            var intent= Intent(it.context,DistanceToTerrenoActivity::class.java)
            intent.putExtra("post",publicacion)
            startActivity(intent)
        }

        binding.enviarMensajeButton.setOnClickListener {
            Log.w("IDS-FOR-MESSAGE", "UserUID: ${authService.getCurrentUser()!!.uid}, ${this.publicacion.uid}")
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
}