package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView

class SearchByLocationActivity : AppCompatActivity() {

    var terrenosController: PublicacionController= PublicacionController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_location)
        var terrenos= terrenosController.getTerrenos(this.assets.open(("terrenos.json")))
        var terrenosGrid: GridView= findViewById(R.id.terrenosGridxUbicacion)
        terrenosGrid.adapter= PublicacionAdapter(this,terrenos)
        terrenosGrid.setOnItemClickListener { parent, view, position, id ->
            intent= Intent(this,VerPublicacionActivity::class.java)
            intent.putExtra("publicacion",terrenos.elementAt(position))
            startActivity(intent)
        }
    }
}