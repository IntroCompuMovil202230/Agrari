package com.example.agrari

import org.json.JSONObject
import java.io.Serializable

class Publicacion : Serializable {

    lateinit  var titulo:String
    lateinit  var ubicacion:String
    var precio:Int = 0
    lateinit  var imagen:String

    constructor(json: JSONObject){
        this.titulo= json.getString("titulo")
        this.ubicacion= json.getString("ubicacion")
        this.precio= json.getString("precio").toInt()
        this.imagen= json.getString("imagen")
    }
}