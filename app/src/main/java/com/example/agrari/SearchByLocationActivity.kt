package com.example.agrari

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.agrari.Model.AgrariPost
import com.example.agrari.services.DB_Service

class SearchByLocationActivity : AppCompatActivity() {

    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null
    private  lateinit var dbService: DB_Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_location)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
        this.dbService= DB_Service()

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

        var terrenosGrid: GridView= findViewById(R.id.terrenosGridxUbicacion)

        this.dbService.getAllPosts().addSnapshotListener { value, error ->

            Log.w("LISTENER-DATA", "Listening the data...")

            if (error != null) {
                Log.w("LISTENER-ERROR", "Listen failed.", error)
                return@addSnapshotListener
            }

            var posts= mutableListOf<AgrariPost>()
            for (doc in value!!) {
                Log.i("USERS-COLLECTION-DATA", "NAME: ${doc.getString("name")}")
                posts.add(AgrariPost(doc))
            }

            terrenosGrid.adapter= PublicacionAdapter(this,posts)
            terrenosGrid.setOnItemClickListener { parent, view, position, id ->
                intent= Intent(this,VerPublicacionActivity::class.java)
                intent.putExtra("post",posts.elementAt(position))
                startActivity(intent)
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
}