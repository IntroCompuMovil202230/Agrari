package com.example.mapstest

import android.app.Activity
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.example.agrari.controller.DirectionHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import org.json.JSONObject
import java.net.URL

class MapController(currentActivity: Activity, googleMap:GoogleMap, currentLocation: LatLng){
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
        var url: String =
            getUrl(this.currentLocationMarket.position, secondLocation)
        Log.i("URL-PATH-DIRECTIONS", url)
        var json= URL(url).readText()

        var data: String = json
        val routes: List<List<HashMap<String, String>>>? = null

        val jsonObject: JSONObject = JSONObject(data)

        var helper: DirectionHelper = DirectionHelper()

        var points: MutableList<LatLng> = helper.parse(jsonObject)

        drawRoutefromPoints(points)

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
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val parameters = "$str_origin&$str_dest&$sensor"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyBAY8r_qu-CnUfQHIDucawooxgLq7vlFV8"
    }




}