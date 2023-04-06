package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if(!isLocationEnabled()){
            Toast.makeText(this,"Your location Provider is turn off  Please turn it on",Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(this,"Your location Provider is Already ON",Toast.LENGTH_LONG).show()
        }


    }

    private fun isLocationEnabled():Boolean{
        //This provide access to the system loaction services
        val locationManager: LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}