package com.example.agrari

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.squareup.picasso.Picasso

class VerPublicacionActivity : AppCompatActivity() {
    lateinit var publicacion: Publicacion
    lateinit var publicacionTitulo: TextView
    lateinit var publicacionUbicacion: TextView
    lateinit var publicacionPrecio: TextView
    lateinit var publicacionImagen: ImageView
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_publicacion)

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
        publicacion= intent.getSerializableExtra("publicacion") as Publicacion
        publicacionImagen=findViewById(R.id.infoPublicacionImagen)
        publicacionTitulo= findViewById(R.id.infoPublicacionTitulo)
        publicacionUbicacion= findViewById(R.id.infoPublicacionUbicacion)
        publicacionPrecio= findViewById(R.id.infoPublicacionPrecio)
        publicacionTitulo.text= publicacion.titulo
        publicacionUbicacion.text= publicacion.ubicacion
        publicacionPrecio.text= " $ "+publicacion.precio
        Picasso.get().load(publicacion.imagen).into(publicacionImagen)

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