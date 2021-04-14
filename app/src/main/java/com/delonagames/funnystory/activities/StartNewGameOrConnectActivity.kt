package com.delonagames.funnystory.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.host.HostActivity
import com.delonagames.funnystory.clientapi.NetworkService
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException

class StartNewGameOrConnectActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonConnectLastGame: Button

    private lateinit var funnyStoryApp: FunnyStoryApp
    private var gameId = 0
    private var userId = 0

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
        hideProgressBar()
        //finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_new_game_or_connect)

        initData()
        buildGUI()
    }

    private fun initData(){
        funnyStoryApp = applicationContext as FunnyStoryApp
        userId = funnyStoryApp.userId
    }

    private fun buildGUI(){

        editText = findViewById(R.id.editText)

        progressBar = findViewById(R.id.progressBar)

        val textView: TextView = findViewById(R.id.textViewUserId)
        textView.text = java.lang.String.format("Ваш ID для сетевых игры: %s", userId.toString())

        val buttonConnect: Button = findViewById(R.id.buttonConnect)
        buttonConnect.setOnClickListener {
            funnyStoryApp.isHost = false
            funnyStoryApp.gameWasStarted = true
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                checkInfoAndConnectToGame()
                hideProgressBar()
            }
        }

        buttonConnectLastGame = findViewById(R.id.buttonConnectLast)
        buttonConnectLastGame.setOnClickListener {
            if (funnyStoryApp.isHost) {
                goToHostActivity()
            } else {
                goToWaitingActivity()
            }
        }

        val buttonNew: Button = findViewById(R.id.buttonNewGame)
        buttonNew.setOnClickListener {
            funnyStoryApp.gameWasStarted = true
            funnyStoryApp.isHost = true
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                startNewGame()
                hideProgressBar()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        buttonConnectLastGame.apply {
            visibility = if (funnyStoryApp.gameWasStarted) {
                View.VISIBLE
            } else {
                View.INVISIBLE
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
            val response = client.addUserInGame(gameId, userId)
            if (response.isSuccessful) {
                funnyStoryApp.gameId = gameId
                goToWaitingActivity()
            } else {
                val BAD_REQUEST = 400
                if (response.code() == BAD_REQUEST) {
                    withContext(Dispatchers.Main) {
                        showToast("Невозможно присоединиться к игре. Игра уже началась или еще не создана")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Неверный запрос $response")
                    }
                }
            }
        }
    }

    private fun goToWaitingActivity() {
        intent = Intent(this, WaitingActivity::class.java)
        startActivity(intent)
    }

    private suspend fun startNewGame() {
        withContext(Dispatchers.IO) {
            getNewGameIdAndStartHostActivity()
        }
    }

    private suspend fun getNewGameIdAndStartHostActivity() {
        withContext(Dispatchers.IO) {
            val response = client.createNewGameAddHostAndGetGameId(userId)
            if (response.isSuccessful && response.body() != null) {
                funnyStoryApp.gameId = response.body()!!
                goToHostActivity()
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Неверный запрос $response")
                }
            }
        }
    }

    private fun goToHostActivity() {
        intent = Intent(this, HostActivity::class.java)
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

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineScope.coroutineContext.cancelChildren()
    }

}