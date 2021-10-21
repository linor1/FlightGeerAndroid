package com.example.android.roomwordssample
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {

    @GET("/screenshot")
    fun getImg(): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("api/command")
    fun post(@Body command : Command): Call<ResponseBody>
}