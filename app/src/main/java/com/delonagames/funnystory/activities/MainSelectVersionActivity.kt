package com.delonagames.funnystory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity
import com.delonagames.funnystory.clientapi.NetworkService
import kotlinx.coroutines.*
import java.net.ConnectException

class MainSelectVersionActivity : AppCompatActivity() {

    private lateinit var buttonLocal: Button
    private lateinit var buttonNetwork: Button

    private lateinit var funnyStoryApp: FunnyStoryApp

    private val client = NetworkService.retrofitService
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        val msg = when (exception) {
            is ConnectException -> "Проблемы с сервером. "
            else -> "Что-то пошло не так."
        }
        exception.printStackTrace()
        showToast("$msg $exception")
        coroutineScope.coroutineContext.cancelChildren()
        //finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_select_version)

        funnyStoryApp = applicationContext as FunnyStoryApp

        buildGUI()
    }

    private fun buildGUI() {
        buttonLocal = findViewById(R.id.buttonLocal)
        buttonLocal.setOnClickListener {
            goToLocal()
        }

        buttonNetwork = findViewById(R.id.buttonNetwork)
        buttonNetwork.setOnClickListener {
            coroutineScope.launch(coroutineExceptionHandler) {
                connectToServerAndGetId()
            }
        }
    }

    private fun goToLocal() {
        intent = Intent(this, CreateSentenceActivity::class.java)
        funnyStoryApp.networkVersion = false
        startActivity(intent)
    }

    private suspend fun connectToServerAndGetId() {
        withContext(Dispatchers.IO) {
            val response = client.getNewUserId()
            if (response.isSuccessful && response.body() != null) {
                funnyStoryApp.userId = response.body()!!
                funnyStoryApp.networkVersion = true
                funnyStoryApp.gameWasStarted = false
                goToNetwork()
            } else {
                showToast("Неверный запрос $response")
            }
        }
    }

    private fun goToNetwork() {
        intent = Intent(this, StartNewGameOrConnectActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

}