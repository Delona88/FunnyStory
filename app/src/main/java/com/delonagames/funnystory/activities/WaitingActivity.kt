package com.delonagames.funnystory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity
import com.delonagames.funnystory.clientapi.RetrofitInterfaceApi
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException

class WaitingActivity : AppCompatActivity() {

    private lateinit var funnyStoryApp: FunnyStoryApp

    private lateinit var client: RetrofitInterfaceApi

    private var coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var gameId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        funnyStoryApp = applicationContext as FunnyStoryApp
        client = funnyStoryApp.client
        gameId = funnyStoryApp.gameId

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            val msg = when (exception) {
                is ConnectException -> "Сервер не запущен. "
                is SocketTimeoutException -> "Сервер не отвечает. "
                else -> "Что-то пошло не так. "
            }
            exception.printStackTrace()
            showToast("$msg $exception")
            coroutineScope.coroutineContext.cancelChildren()
            finish()
        }

        coroutineScope.launch(coroutineExceptionHandler) {
            tryToStartGame()
        }
    }

    private suspend fun tryToStartGame() {
        withContext(Dispatchers.IO) {
            var isGameActive = false
            while (!isGameActive) {
                val response = client.isGameActive(gameId)
                if (response.isSuccessful && response.body() != null) {
                    isGameActive = response.body()!!
                } else {
                    isGameActive = true
                    withContext(Dispatchers.Main) {
                        showToastServerProblem()
                    }
                }
                delay(500)
            }
            goToCreateSentenceActivity()
        }
    }

    private fun showToastServerProblem() {
        Toast.makeText(this, "Проблемы с сервером. Попробуйте позже", Toast.LENGTH_LONG).show()
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    private fun goToCreateSentenceActivity() {
        intent = Intent(this, CreateSentenceActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineScope.coroutineContext.cancelChildren()
    }
}