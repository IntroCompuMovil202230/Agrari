package com.example.mapstest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task


class LocationController(currentActivity: Activity,  mFusedLocationClient:FusedLocationProviderClient) {

    var currentActivity: Activity

    var mFusedLocationClient: FusedLocationProviderClient
    var mLocationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback



    init {
        this.currentActivity= currentActivity
        this.mFusedLocationClient=mFusedLocationClient
        this.mLocationRequest=createLocationRequest()
    }


    fun checkLocationStatus():Task<LocationSettingsResponse>{
        val locationRequest: LocationRequest.Builder = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest.build())
        val client = LocationServices.getSettingsClient(this.currentActivity)
        val task = client.checkLocationSettings(builder.build())
        return task
    }


    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000,
        ).build()
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this.currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, this.locationCallback, null)
        }
    }


    fun stopLocationUpdates() {
        this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

}




/*

class LocationController(currentActivity: Activity, locationManager: FusedLocationProviderClient) {

    var mFusedLocationClient: FusedLocationProviderClient
    var currentActivity: Activity
    var locationManager: LocationManager
    var mLocationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    init {
        this.locationManager=locationManager
        this.currentActivity= currentActivity
        this.mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this.currentActivity)
        this.mLocationRequest= createLocationRequest()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun getCurrentLocation():Task<Location>{
        return this.mFusedLocationClient.lastLocation
    }


     fun checkLocationSettings():Task<LocationSettingsResponse>{
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this.currentActivity)
        val task = client.checkLocationSettings(builder.build())
        return task
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }



    @SuppressLint("MissingPermission", "SetTextI18n")
    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this.currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, this.locationCallback, null)
        }
    }


    fun stopLocationUpdates() {
        this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
    }


}
*/