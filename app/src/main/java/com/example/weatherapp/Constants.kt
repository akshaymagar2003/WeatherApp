package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object  Constants{
  const val  appID: String ="fbf185829a8636a7d0c577dc1b247556"
    const val Base_URL:String="https://api.openweathermap.org/data/"
    const val Matric_unit:String="metric"
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
      val network = connectivityManager.activeNetwork ?: return false
      val activeNetwork = connectivityManager.getNetworkCapabilities(network)

      if (activeNetwork != null) {
          return when {
              activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
              activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
              activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
              else -> false
          }
      }
  }  else{
         val networkInfo=connectivityManager.activeNetworkInfo
          return networkInfo!=null && networkInfo.isConnectedOrConnecting

  }
  return false
    }



}