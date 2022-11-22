package com.example.agrari
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.agrari.Model.AgrariPost
import com.example.agrari.databinding.ActivitySearchByLocationBinding
import com.example.agrari.services.DB_Service
import org.json.JSONArray


class SearchByLocationActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchByLocationBinding
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null
    private  lateinit var dbService: DB_Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchByLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

        this.dbService= DB_Service()

        consumeData()




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

        binding.button2.setOnClickListener {
            val dept: String = binding.spinner.getSelectedItem().toString()
            println(dept)
            this.dbService.getPostByDepartamento(dept).addSnapshotListener { value, error ->

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
                binding.spinner.adapter = adapter

            },
            {})

        queue.add(stringRequest)
    }


}