package com.delonagames.funnystory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity
import com.delonagames.funnystory.clientapi.NetworkService
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException

class WaitingActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    private lateinit var funnyStoryApp: FunnyStoryApp
    private var gameId = 0

    private val client = NetworkService.retrofitService
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        initData()
        buildGUI()
    }

    private fun initData() {
        funnyStoryApp = applicationContext as FunnyStoryApp
        gameId = funnyStoryApp.gameId
    }

    private fun buildGUI() {
        progressBar = findViewById(R.id.progressBar)

        val textView: TextView = findViewById(R.id.textView)
        textView.text = java.lang.String.format("Вы присоединились к игре %s", gameId.toString())
    }

    override fun onStart() {
        super.onStart()

        coroutineScope.launch(coroutineExceptionHandler) {
            showProgressBar()
            waitGameActiveAndGoToCreateSentenceActivity()
            hideProgressBar()
        }
    }

    private suspend fun waitGameActiveAndGoToCreateSentenceActivity() {
        withContext(Dispatchers.IO) {
            var isGameActive = false
            while (!isGameActive) {
                Log.d("tryToStartGame", ".............tryToStartGame")
                val response = client.isGameActive(gameId)
                if (response.isSuccessful && response.body() != null) {
                    isGameActive = response.body()!!
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Игра не найдена.")
                        finish()
                    }
                    break
                }
                delay(500)
            }
            if (isGameActive) goToCreateSentenceActivity()
        }
    }

    private fun goToCreateSentenceActivity() {
        intent = Intent(this, CreateSentenceActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun onStop() {
        super.onStop()

        hideProgressBar()
        coroutineScope.coroutineContext.cancelChildren()
    }

}