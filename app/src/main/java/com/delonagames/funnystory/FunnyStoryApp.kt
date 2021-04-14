package com.delonagames.funnystory

import android.app.Application
import com.delonagames.funnystory.clientapi.FunnyStoryApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FunnyStoryApp : Application() {
    private val baseUrl = "http://192.168.1.52:8080/" // http://192.168.1.52:8080/ https://funnystory.herokuapp.com:443/
    val client: FunnyStoryApi
    var gameId: Int = 0
    var userId: Int = 0
    var networkVersion = false
    var isHost = false
    var gameWasStarted = false

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            //.addCallAdapterFactory(CoroutineCallAdapterFactory())
            //.addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        client = retrofit.create(FunnyStoryApi::class.java)
    }
}