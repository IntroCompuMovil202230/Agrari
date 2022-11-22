package com.example.agrari

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.agrari.Model.AgrariPost
import com.example.agrari.databinding.ActivityAdvanceSearchBinding
import com.example.agrari.databinding.ActivityChoosePostLocationBinding
import com.example.agrari.services.DB_Service
import org.json.JSONArray

class AdvanceSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdvanceSearchBinding
    var terrenosController: PublicacionController= PublicacionController()
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null
    private  lateinit var dbService: DB_Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_search)

        binding = ActivityAdvanceSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        consumeData()

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

            //binding.usersGrid.adapter= MovilUserAdapter(this,movilUsers)
            binding.terrenosGridxAdvanceSearch.adapter= PublicacionAdapter(this,posts)
            binding.terrenosGridxAdvanceSearch.setOnItemClickListener { parent, view, position, id ->
                intent= Intent(this,VerPublicacionActivity::class.java)
                intent.putExtra("post",posts.elementAt(position))
                startActivity(intent)
            }
        }


        binding.button5.setOnClickListener {
            this.dbService.getPostByDepartamentoCategoryRange(
                binding.spinner3.getSelectedItem().toString(),
                binding.spinner5.getSelectedItem().toString(),
                binding.rangeSlider.values.get(0).toDouble(),
                binding.rangeSlider.values.get(1).toDouble()
            ).addSnapshotListener { value, error ->

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

                //binding.usersGrid.adapter= MovilUserAdapter(this,movilUsers)
                binding.terrenosGridxAdvanceSearch.adapter= PublicacionAdapter(this,posts)
                binding.terrenosGridxAdvanceSearch.setOnItemClickListener { parent, view, position, id ->
                    intent= Intent(this,VerPublicacionActivity::class.java)
                    intent.putExtra("post",posts.elementAt(position))
                    startActivity(intent)
                }
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


    fun consumeData(){
        val queue = Volley.newRequestQueue(this)
        var url= "https://www.datos.gov.co/resource/xdk5-pm3f.json?\$select=distinct departamento"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val rootObject= JSONArray(response)
                var departamentos= mutableListOf<String>()
                Log.w("VOLLEY RESPONSE: ", "${rootObject}")
                for (i in 0 until rootObject.length()) {
                    val dept = rootObject.getJSONObject(i)
                    println("${dept.get("departamento")}")
                    departamentos.add(dept.get("departamento").toString())
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos)
                binding.spinner3.adapter = adapter

            },
            {})

        queue.add(stringRequest)
    }


}