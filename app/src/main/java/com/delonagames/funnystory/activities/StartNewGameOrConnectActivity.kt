package com.delonagames.funnystory.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.host.HostActivity
import com.delonagames.funnystory.clientapi.RetrofitInterfaceApi
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException

class StartNewGameOrConnectActivity : AppCompatActivity() {

    private var coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private lateinit var editText: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var funnyStoryApp: FunnyStoryApp

    private lateinit var client: RetrofitInterfaceApi

    private var gameId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_new_game_or_connect)

        editText = findViewById(R.id.editText)
        progressBar = findViewById(R.id.progressBar)

        funnyStoryApp = applicationContext as FunnyStoryApp
        client = funnyStoryApp.client

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

        val buttonConnect: Button = findViewById(R.id.buttonConnect)
        buttonConnect.setOnClickListener {
            funnyStoryApp.host = false
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                checkInfoAndConnectToGame()
                hideProgressBar()
            }
        }

        val buttonNew: Button = findViewById(R.id.buttonNewGame)
        buttonNew.setOnClickListener {
            funnyStoryApp.host = true
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                startNewGame()
                hideProgressBar()
            }
        }
    }

    private suspend fun checkInfoAndConnectToGame() {
        val infoCorrect = checkCorrectInfoAndShowToast()
        if (infoCorrect) {
            gameId = getText().toInt()
            connectToGame()
        }
    }

    private fun checkCorrectInfoAndShowToast(): Boolean {
        if (isInfoEntered()) {
            if (isNumeric(getText())) {
                return true
            } else {
                showToast("Введите число")
            }
        } else {
            showToast("Введите id игры")
        }
        return false
    }



    private suspend fun connectToGame() {
        withContext(Dispatchers.IO) {
            val response = client.connectToGameAndGetUserId(gameId)
            if (response.isSuccessful && response.body() != null) {
                funnyStoryApp.gameId = gameId
                funnyStoryApp.userId = response.body()!!
                goToWaitingActivity()
            } else {
                val BAD_REQUEST = 400
                if (response.code() == BAD_REQUEST) {
                    withContext(Dispatchers.Main) {
                        showToast("Невозможно присоединиться к игре. Игра уже началась или еще не создана")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToastServerProblem()
                    }
                }
            }
        }
    }

    private suspend fun startNewGame() {
        withContext(Dispatchers.IO) {
            getNewGameId()
            goToStartNewGameActivity()
        }
    }

    private suspend fun getNewGameId() {
        withContext(Dispatchers.IO) {
            val response = client.getGameId()
            if (response.isSuccessful && response.body() != null) {
                funnyStoryApp.gameId = response.body()!!
                funnyStoryApp.userId = 0
            } else {
                showToastServerProblem()
            }
        }
    }

    private fun goToStartNewGameActivity() {
        intent = Intent(this, HostActivity::class.java)
        startActivity(intent)
    }

    private fun goToWaitingActivity() {
        intent = Intent(this, WaitingActivity::class.java)
        startActivity(intent)
    }

    private fun getText(): String = editText.text.toString()

    private fun isInfoEntered(): Boolean = getText().isNotEmpty()

    private fun isNumeric(strNum: String): Boolean {
        return try {
            strNum.toInt()
            true
        } catch (nfe: NumberFormatException) {
            false
        }
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    private fun showToastServerProblem() {
            Toast.makeText(this, "Проблемы с сервером. Попробуйте позже", Toast.LENGTH_LONG).show()
    }

    private fun showProgressBar() {
        Log.d("showProgressBar", "showProgressBar")
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        Log.d("hideProgressBar", "hideProgressBar")
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineScope.coroutineContext.cancelChildren()
    }


}