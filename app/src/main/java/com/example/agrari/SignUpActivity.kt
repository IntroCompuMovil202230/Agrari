package com.example.agrari

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.agrari.controller.SignUpController
import com.example.agrari.services.AuthService
import com.example.agrari.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {


    lateinit var binding: ActivitySignUpBinding
    lateinit var signUpController: SignUpController
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        signUpController= SignUpController()

        this.binding.signUpContinueButton.setOnClickListener(View.OnClickListener {

            try {
                this.signUpController.signUpWithEmailPassword(this.binding.signUpEmailInput.text.toString(),this.binding.signUpPasswordInput.text.toString()).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Log.w("SIGN-UP-CONTROLLER", "signInWithEmail:failed", it.exception)
                        Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                        this.binding.signUpEmailInput.setText("");
                        this.binding.signUpPasswordInput.setText("");
                    }else{
                        var intent=Intent(this,SignUpRamainInfoActivity::class.java)
                        intent.putExtra("user_email",it.result.user!!.email);
                        intent.putExtra("user_id",it.result.user!!.uid);
                        startActivity(intent)
                    }
                }
            }catch (e:Exception){
                Toast.makeText(this,e.toString().substring(21),Toast.LENGTH_LONG).show()
                this.binding.signUpEmailInput.setText("");
                this.binding.signUpPasswordInput.setText("");
            }

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