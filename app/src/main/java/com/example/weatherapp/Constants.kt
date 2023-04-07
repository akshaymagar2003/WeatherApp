package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager

object  Constants{

    fun isNetworkAvailable(context: Context):Boolean{
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

     val networkInfo=connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnectedOrConnecting

    }



}