package com.example.agrari.services

import android.app.Activity
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.example.agrari.R
import com.example.agrari.controller.RouteController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.maps.android.SphericalUtil
import org.json.JSONObject
import java.net.URL

class MapService(currentActivity: Activity, googleMap:GoogleMap, justOneNewMarker: Boolean?){
    var currentActivity: Activity
    var mMap:GoogleMap
    private var currentLocationMarket: Marker?=null
    var secondLocationMarket: Marker?=null
    private var currentPolyline: Polyline?=null
    private  var markersCoordinates: ArrayList<LatLng> = ArrayList()
    private  var dbService= DB_Service()

    init {
        this.currentActivity= currentActivity
        this.mMap=googleMap
        this.mMap.uiSettings.isZoomGesturesEnabled = true
        this.mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMapLongClickListener {
            Log.i("ON-LONG-CLICK", "Latitude "+it.latitude)
            Log.i("ON-LONG-CLICK", "Longitude "+it.longitude)
           if(justOneNewMarker!=null){
               if (justOneNewMarker){
                   addMJustOnearker(it,"marker")
               }else{
                   addMarker(it,"marker")
               }
           }
        }
    }

    fun updateCurrentPositionMarket(curretLocation:LatLng, moveCamera:Boolean, currentUser: FirebaseUser?){

        var distanceBeforeChange:Float

        try {
            if(this.currentLocationMarket!=null){
                this.currentLocationMarket!!.remove()
            }

            distanceBeforeChange = if(this.currentLocationMarket==null){
                0F
            }else{
                this.distance(this.currentLocationMarket!!.position,curretLocation)
            }
            //Log.i("MAP-UPDATE-POSITION-MARKET", "Updating current Market.")
            this.currentLocationMarket= this.mMap.addMarker(MarkerOptions().position(curretLocation).title("You are here!"))!!
            if(moveCamera || distanceBeforeChange>5){
                this.mMap.moveCamera(CameraUpdateFactory.newLatLng(curretLocation))
                mMap.moveCamera(CameraUpdateFactory.zoomTo(13F))
                if(currentUser!=null){
                    this.dbService.updateLocation(currentUser.uid,curretLocation)
                    if(this.secondLocationMarket!=null){
                        this.drawRoute(this.secondLocationMarket!!.position)
                    }
                }
            }
        }catch (_:Exception){}
    }


    fun addMarker(markerLocation:LatLng, title:String){
        this.markersCoordinates.add(markerLocation)
        var newMarker= this.mMap.addMarker(
            MarkerOptions()
                .position(markerLocation)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
    }


    fun addMJustOnearker(markerLocation:LatLng, title:String){
        if(secondLocationMarket!=null){
            secondLocationMarket!!.remove()
        }
        secondLocationMarket= this.mMap.addMarker(
            MarkerOptions()
                .position(markerLocation)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
    }



    fun  updateSecondMarker(markerLocation:LatLng, title:String){
        if(this.secondLocationMarket!=null){
            this.secondLocationMarket!!.remove()
        }
        secondLocationMarket= this.mMap.addMarker(
            MarkerOptions()
                .position(markerLocation)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

        if(this.currentLocationMarket!=null){
            this.drawRoute(markerLocation)
        }
    }




    private fun distance(p1:LatLng, p2:LatLng): Float {
        var point1: Location= Location("localtion1")
        point1.latitude=p1.latitude
        point1.longitude=p1.longitude
        var point2: Location= Location("localtion2")
        point2.latitude=p2.latitude
        point2.longitude=p2.longitude
        var distance:Float= point1.distanceTo(point2)
        if(distance==0F){
            Log.i("DISTANCE", "0 CURRENT DISTANCE")
        }else{
            Log.i("DISTANCE", "*******CURRENT DISTANCE: $distance **********")
        }
        return distance
    }


    fun drawRoute(secondLocation:LatLng) {
        try {
            var url: String = getUrl(this.currentLocationMarket!!.position,secondLocation)
            Log.i("URL-PATH-DIRECTIONS", url)
            var json= URL(url).readText()
            var data: String = json
            val routes: List<List<HashMap<String, String>>>? = null
            val jsonObject: JSONObject = JSONObject(data)
            var routeController=RouteController()
            var points: MutableList<LatLng> = routeController.parse(jsonObject)
            println(points)
            drawRoutefromPoints(points)
        }catch (e:Exception){
            Toast.makeText(this.currentActivity,"No path found for the current points sorry :(",Toast.LENGTH_LONG).show()
        }
    }


    fun drawRoutefromPoints(points: MutableList<LatLng>){
        if(this.currentPolyline!=null){
            this.currentPolyline!!.remove()
        }
        this.currentPolyline = this.mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .color(Color.RED).width(12F)
                .addAll(points.toList()))
    }

    fun getUrl(origin: LatLng, dest: LatLng): String{
        val originLat= origin.latitude
        val originLon= origin.longitude
        val endLat= dest.latitude
        val endLon= dest.longitude
        val API_KEY= "5b3ce3597851110001cf6248ee91b2bd239241bbb6193af9a68ec224"
        return "https://api.openrouteservice.org/v2/directions/driving-car?api_key=$API_KEY&start=${originLon},${originLat}&end=${endLon},${endLat}"
    }


    fun paintMarkersPolygon():Double{
        val polygon1 = mMap.addPolygon(PolygonOptions()
            .clickable(true)
            .addAll(this.markersCoordinates)
            .fillColor(Color.parseColor("#FF03DAC5"))
            .strokeJointType(JointType.ROUND)
        )
        var area:Double= SphericalUtil.computeArea(this.markersCoordinates)
        var intArea= area.toInt()
        Log.i("AREA-MAP", "computeArea " + area.toInt())
        return area
    }



    fun changeMapLuminocity(eventValue:Float){
        if (eventValue < 10000) {
            this.mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this.currentActivity,
                    R.raw.dark_style))
        } else {
            this. mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this.currentActivity,
                    R.raw.light_style
                )
            )
        }
    }

}





/*
* class MapService(currentActivity: Activity, googleMap:GoogleMap, currentLocation: LatLng){
    var currentActivity: Activity
    var mMap:GoogleMap
    var currentLocationMarket: Marker
    private var currentPolyline: Polyline?=null
    private  var markersCoordinates: ArrayList<LatLng> = ArrayList()

    init {
        Log.i("MAP-CONTROLLER-INIT", "INIT MAP CONTROLLER")
        this.currentActivity= currentActivity
        this.mMap=googleMap
        mMap = googleMap
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20F))
        this.currentLocationMarket= mMap.addMarker(MarkerOptions().position(currentLocation).title("CurrentLocationMarker"))!!
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        mMap.setOnMapLongClickListener {
            Log.i("ON-LONG-CLICK", "Latitude "+it.latitude)
            Log.i("ON-LONG-CLICK", "Longitude "+it.longitude)
            addMarker(it)
        }
    }

    fun updateCurrentPositionMarket(curretLocation:LatLng){
        try {
            var dis= this.distance(this.currentLocationMarket.position,curretLocation)
            if(dis>1){
                Log.i("DISTANCE", "THE DISTANCE IS: ${dis}")
                this.currentLocationMarket.remove()
                this.currentLocationMarket= this.mMap.addMarker(MarkerOptions().position(curretLocation).title("CurrentLocationMarker"))!!
                this.mMap.moveCamera(CameraUpdateFactory.newLatLng(curretLocation))
            }
        }catch (_:Exception){}
    }

     fun addMarker(markerLocation:LatLng){
         this.markersCoordinates.add(markerLocation)
        var newMarker= this.mMap.addMarker(
                MarkerOptions()
                    .position(markerLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocation))
         /*Toast.makeText(this.currentActivity,"Distance: ${this.distance(this.currentLocationMarket.position,
             newMarker!!.position)}",Toast.LENGTH_LONG).show() */
    }

    private fun distance(p1:LatLng, p2:LatLng): Float {
        var point1: Location= Location("localtion1")
        point1.latitude=p1.latitude
        point1.longitude=p1.longitude
        var point2: Location= Location("localtion2")
        point2.latitude=p2.latitude
        point2.longitude=p2.longitude
        return point1.distanceTo(point2)
    }


    fun drawRoute(secondLocation:LatLng) {
        try {
            var url: String = getUrl(this.currentLocationMarket.position,secondLocation)
            Log.i("URL-PATH-DIRECTIONS", url)
            var json= URL(url).readText()
            var data: String = json
            val routes: List<List<HashMap<String, String>>>? = null
            val jsonObject: JSONObject = JSONObject(data)
            var routeController= RouteController()
            var points: MutableList<LatLng> = routeController.parse(jsonObject)
            println(points)
            drawRoutefromPoints(points)
        }catch (e:Exception){
            Toast.makeText(this.currentActivity,"No path found for the current points sorry :(",
                Toast.LENGTH_LONG).show()
        }
    }

    fun paintMarkersPolygon():Double{
        val polygon1 = mMap.addPolygon(PolygonOptions()
            .clickable(true)
            .addAll(this.markersCoordinates)
            .fillColor(Color.parseColor("#FF03DAC5"))
            .strokeJointType(JointType.ROUND)
        )
            var area:Double= SphericalUtil.computeArea(this.markersCoordinates)
        var intArea= area.toInt()
        Log.i("AREA-MAP", "computeArea " + area.toInt())
        return area
    }

    fun drawRoutefromPoints(points: MutableList<LatLng>){
        if(this.currentPolyline!=null){
            this.currentPolyline!!.remove()
        }
        this.currentPolyline = this.mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .color(Color.RED)
                .addAll(points.toList()))
    }


    fun getUrl(origin: LatLng, dest: LatLng): String{
        val originLat= origin.latitude
        val originLon= origin.longitude
        val endLat= dest.latitude
        val endLon= dest.longitude
        val API_KEY= "5b3ce3597851110001cf6248ee91b2bd239241bbb6193af9a68ec224"
        return "https://api.openrouteservice.org/v2/directions/driving-car?api_key=$API_KEY&start=${originLon},${originLat}&end=${endLon},${endLat}"
    }

    fun changeMapLuminocity(eventValue:Float){
        if (eventValue < 10000) {
            this.mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this.currentActivity,
                    R.raw.dark_style))
        } else {
            this. mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this.currentActivity,
                    R.raw.light_style
                )
            )
        }
    }



}
*
*
*
*
* */