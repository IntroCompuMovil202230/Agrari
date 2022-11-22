package com.example.agrari.Model

import com.example.agrari.services.CredentialValidator
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.QueryDocumentSnapshot
import org.checkerframework.checker.units.qual.Temperature
import java.io.Serializable
import java.math.BigDecimal

class AgrariPost: Serializable {

    lateinit var uid:String
    var seller_uid:String
    var title: String
    var price: Int
    var image:String
    var latitude: Float= 0F
    var longitude: Float =0F
    var area: Float =0F
    var humidity: Float=0F
    var temperature: Float=0F
    var category : String
    lateinit var departamento : String

    constructor(seller_uid: String, title: String, price: Int, image: String, category: String) {

        if(title.isEmpty()){
            throw Exception("Invalid title")
        }else if(price==0){
            throw Exception("Invalid price")
        }else if(image.isEmpty()){
            throw Exception("Invalid image")
        }else if(category.isEmpty()){
            throw Exception("Invalid  category")
        }else{
            this.seller_uid = seller_uid
            this.title = title
            this.price = price
            this.image = image
            this.category = category
        }
    }



    constructor(doc: QueryDocumentSnapshot){
        this.uid =doc.getString("uid")!!
        this.seller_uid = doc.getString("seller_uid")!!
        this.title = doc.getString("title")!!
        this.price = doc.getDouble("price")!!.toInt()
        this.image = doc.getString("image")!!
        this.latitude = doc.getDouble("latitude")!!.toFloat()
        this.longitude = doc.getDouble("longitude")!!.toFloat()
        this.area = doc.getDouble("area")!!.toFloat()
        this.humidity = doc.getDouble("humidity")!!.toFloat()
        this.temperature = doc.getDouble("temperature")!!.toFloat()
        this.category = doc.getString("category")!!
        this.departamento = doc.getString("departamento")!!
    }


    fun setPostLocationInfo(location:LatLng, departamento: String){
        this.latitude=location.latitude.toFloat()
        this.longitude=location.longitude.toFloat()
        this.departamento=departamento
    }


    fun setAreaHumidityTemperature(area: Float,humidity: Float,temperature: Float){
        this.area=area
        this.humidity=humidity
        this.temperature=temperature
    }





    fun toJson():HashMap<String, Any>{
        return  hashMapOf(
            "uid" to this.uid,
            "seller_uid" to this.seller_uid,
            "title" to this.title,
            "price" to this.price,
            "image" to this.image,
            "latitude" to this.latitude,
            "longitude" to this.longitude,
            "area" to this.area,
            "humidity" to this.humidity,
            "temperature" to this.temperature,
            "category" to this.category,
            "departamento" to this.departamento,
        )
    }







}