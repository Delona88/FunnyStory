package com.delonagames.funnystory
import com.delonagames.funnystory.clientapi.RetrofitInterfaceApi
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException

class TestRetrofitAndServer {
    private lateinit var client: RetrofitInterfaceApi

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    @Before
    fun setUp() {
        val baseUrl = "http://192.168.1.52:8080/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        client = retrofit.create(RetrofitInterfaceApi::class.java)
    }

    @Test
    fun testGetGameId() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        }
        coroutineScope.launch(coroutineExceptionHandler) {
            val currentId = client.getGameId()
            val newId = client.getGameId()
            println("current $currentId newId $newId")
            Assert.assertTrue(currentId.body() == newId.body()!!.minus(1))
        }
    }
}


