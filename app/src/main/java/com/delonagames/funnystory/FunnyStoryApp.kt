package com.delonagames.funnystory

import android.app.Application
import com.delonagames.funnystory.clientapi.RetrofitInterfaceApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

class FunnyStoryApp : Application() {
    private val baseUrl = "http://192.168.1.52:8080/"
    val client: RetrofitInterfaceApi
    var gameId: Int = 0
    var userId: Int = 0
    var networkVersion = false
    var host = false

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            //.addCallAdapterFactory(CoroutineCallAdapterFactory())
            //.addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        client = retrofit.create(RetrofitInterfaceApi::class.java)
    }
}