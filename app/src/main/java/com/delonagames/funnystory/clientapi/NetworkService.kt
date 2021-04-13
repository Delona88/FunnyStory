package com.delonagames.funnystory.clientapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val BASE_URL = "http://192.168.1.52:8080/" // http://192.168.1.52:8080/ https://funnystory.herokuapp.com:443/
    val retrofitService : RetrofitInterfaceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitInterfaceApi::class.java)
    }
}