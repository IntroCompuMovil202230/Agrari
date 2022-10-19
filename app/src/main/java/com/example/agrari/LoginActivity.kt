package com.example.agrari

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.agrari.controller.AuthController
import com.example.agrari.databinding.ActivityLoginBinding
import com.example.agrari.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    var isSeller:Boolean=false;
    lateinit var binding: ActivityLoginBinding
    lateinit var authController: AuthController
    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null
    var lightSensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.authController= AuthController()
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
        binding.recoverPasswordText.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity:: class.java))
        })



        this.binding.loginSwitch.setOnCheckedChangeListener { _, isChecked ->
            this.isSeller = isChecked
        }


        this.binding.login.setOnClickListener(View.OnClickListener {
            try {
                this.authController.signInWithEmailPassword(this.binding.loginEmailInput.text.toString(),this.binding.loginPasswordInput.text.toString()).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Toast.makeText(this,"Correo o contraseña inválida",Toast.LENGTH_LONG).show()
                        Log.w("SIGN-UP-CONTROLLER", "signInWithEmail:failed", it.exception)
                        Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show()
                        this.binding.loginEmailInput.setText("");
                        this.binding.loginPasswordInput.setText("");
                    }else{
                        if(this.isSeller){
                            startActivity(Intent(this, HomeVendedor::class.java))
                        }else{
                            startActivity(Intent(this, HomeActivity::class.java))
                        }
                    }
                }
            }catch (e:Exception){
                Toast.makeText(this,e.toString().substring(21),Toast.LENGTH_LONG).show()
                this.binding.loginEmailInput.setText("");
                this.binding.loginPasswordInput.setText("");
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