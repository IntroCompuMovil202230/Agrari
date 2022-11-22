package com.example.agrari

import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class PublicacionController {


    fun loadJSONFromAssets(fileStream:InputStream):String{
        lateinit var json:String
        try{
            var stream: InputStream = fileStream
            var size: Int = stream.available()
            var buffer= ByteArray(size)
            stream.read(buffer)
            stream.close()
            json= String(buffer)
        }catch (e: IOException){
            e.printStackTrace()
        }
        return json
    }

    fun getTerrenos(fileStream:InputStream): MutableList<Publicacion>{
        var publicaciones= mutableListOf<Publicacion>()
        var json= JSONObject(loadJSONFromAssets(fileStream))
        var jsonCountries= json.getJSONArray("terrenos")
        for (i in 0 until jsonCountries.length()){
            var jsonObject= jsonCountries.getJSONObject(i)
            publicaciones.add(Publicacion(jsonObject))
        }
        return publicaciones
    }

}

//this.assets.open(("paises.json"))