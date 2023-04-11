package com.example.weatherapp.Networks

import com.example.weatherapp.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("3.0/onecall")
    fun getWeather(
        @Query("lat") lat:Double,
      @Query("lon") lon:Double,
        @Query("units") units:String,
        @Query("appID") appID:String
    ): Call<WeatherResponse>



}