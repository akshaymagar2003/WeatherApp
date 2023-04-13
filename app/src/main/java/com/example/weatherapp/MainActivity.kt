package com.example.weatherapp
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.Networks.WeatherService
import com.example.weatherapp.models.WeatherResponse
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    // A fused location client variable which is further used to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
  private var mprogressDialog: Dialog?=null
    val tv_main: TextView? =null
    val tv_main_description: TextView? =null
    val tv_sunset_time: TextView? =null
    val tv_sunrise_time:TextView?=null
    val tv_temp: TextView? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Fused location variable`
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if(!isLocationEnabled()){
            Toast.makeText(this,"Your location Provider is turn off  Please turn it on",Toast.LENGTH_LONG).show()

            val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        else{
            Dexter.withActivity(this)
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {

                            requestLocationData()
                            // END
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have denied location permission. Please allow it is mandatory.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }


    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun isLocationEnabled():Boolean{
        //This provide access to the system loaction services
        val locationManager: LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            val latitude = mLastLocation?.latitude
            Log.i("Current Latitude", "$latitude")

            val longitude = mLastLocation?.longitude
            Log.i("Current Longitude", "$longitude")
            if (latitude != null) {
                if (longitude != null) {
                    getLocationWeatherDetails(latitude,longitude)
                }
            }
        }
    }
    private fun getLocationWeatherDetails(latitude:Double,longitude:Double){
        if(Constants.isNetworkAvailable(this)){

val retrofit :Retrofit= Retrofit.Builder()
                 .baseUrl(Constants.Base_URL)
    .addConverterFactory(GsonConverterFactory.create()).build()

            val service:WeatherService=retrofit.create< WeatherService>(WeatherService::class.java)
        val listCall:Call<WeatherResponse> =service.getWeather(latitude,longitude,Constants.Matric_unit,Constants.appID)

 showCustomProgressDialog()

        listCall.enqueue(object:Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
               if(response!!.isSuccessful){
                   hideProgessDialog()
                   val weatherList: WeatherResponse? =response.body()
                   Log.i("Response Result","$weatherList")

               }else{
                   val rc =response.code()
                   when(rc){
                       400->{
                           Log.i("Error 400","Bad Connection")
                       }
                       404->{
                           Log.e("Error 404","Not Found")
                       }
                       else->{
                           Log.i("Error","Generic Error")
                       }
                   }
               }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
          Log.e("Errorrrr", t.message.toString())
            }

        })




        }



        else{
            Toast.makeText(this@MainActivity,"No internet connection available ",Toast.LENGTH_SHORT).show()
        }
    }

private fun showCustomProgressDialog(){
    mprogressDialog=Dialog(this)
    mprogressDialog!!.setContentView(R.layout.custom_dialog_progressbar)
mprogressDialog!!.show()
}
private fun  hideProgessDialog(){
    if(mprogressDialog!=null){
        mprogressDialog!!.dismiss()
    }
}
  private fun setUpUi(weatherList:WeatherResponse){
         for(i in weatherList.weather.indices){
   Log.i("Weather Name",weatherList.weather.toString())

             tv_main?.text=weatherList.weather[i].main
             tv_main_description?.text=weatherList.weather[i].description
         tv_temp?.text=weatherList.main.temp.toString()+getUnit(application.resources.configuration.locales.toString())
        tv_sunrise_time?.text= unixTime(weatherList.sys.sunrise)
        tv_sunset_time?.text= unixTime(weatherList.sys.sunset)
         }
  }

    private fun getUnit(value: String): String? {

        var value ="°C"
        if("US"==value||"LR"==value||"MM"==value){
            value="°F"
        }
        return value
    }

    private fun unixTime(timex:Long):String?{
     val date= Date(timex *1000L)
        val sdf=SimpleDateFormat("HH:mm",Locale.UK)
        sdf.timeZone= TimeZone.getDefault()
        return sdf.format(date)
    }

}