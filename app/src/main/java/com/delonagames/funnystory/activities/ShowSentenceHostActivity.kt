package com.delonagames.funnystory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity.Sentence.sentence
import com.delonagames.funnystory.activities.host.HostActivity
import com.delonagames.funnystory.activities.host.ListUsersAdapter
import com.delonagames.funnystory.clientapi.NetworkService
import kotlinx.coroutines.*
import java.net.ConnectException

class ShowSentenceHostActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewWord1: TextView
    private lateinit var textViewWord2: TextView
    private lateinit var textViewWord3: TextView
    private lateinit var textViewWord4: TextView
    private lateinit var textViewWord5: TextView
    private lateinit var textViewWord6: TextView
    private lateinit var textViewWord7: TextView
    private lateinit var textViewWord8: TextView
    private lateinit var textViewUsers: TextView
    private lateinit var buttonNewGame: Button
    private lateinit var recyclerView: RecyclerView

    private var networkVersion = false
    private var gameId = 0
    private var userId = 0
    private var host = false

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
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_sentence_host)

        initData()
        buildGUI()
    }

    private fun initData() {
        val funnyStoryApp = applicationContext as FunnyStoryApp
        networkVersion = funnyStoryApp.networkVersion
        gameId = funnyStoryApp.gameId
        userId = funnyStoryApp.userId
        host = funnyStoryApp.isHost
    }

    private fun buildGUI() {
        textViewWord1 = findViewById(R.id.textView1)
        textViewWord2 = findViewById(R.id.textView2)
        textViewWord3 = findViewById(R.id.textView3)
        textViewWord4 = findViewById(R.id.textView4)
        textViewWord5 = findViewById(R.id.textView5)
        textViewWord6 = findViewById(R.id.textView6)
        textViewWord7 = findViewById(R.id.textView7)
        textViewWord8 = findViewById(R.id.textView8)

        textViewWord1.text = "Дождитесь пока все игроки отправят свои истории"

        textViewUsers = findViewById(R.id.textViewUsers)

        progressBar = findViewById(R.id.progressBar)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonNewGame = findViewById(R.id.buttonNew)
        buttonNewGame.visibility = View.INVISIBLE
        buttonNewGame.setOnClickListener {
            startNewGame()
        }

    }

    override fun onStart() {
        super.onStart()

        if (networkVersion) {
            startNetworkVersion()
        } else {
            textViewWord1.text = sentence.getStringSentence()
            showButtonNewGame()
        }
    }

    private fun startNetworkVersion() {
        coroutineScope.launch(coroutineExceptionHandler) {
            //showProgressBar()
            sendSentenceGetAndShowNewSentence()
            //hideProgressBar()
        }
    }


    private suspend fun sendSentenceGetAndShowNewSentence() {
        withContext(Dispatchers.IO) {
            val response = client.setSentence(gameId, userId, sentence.toListOfStrings())
            if (response.isSuccessful) {
                waitGameOverGetSentenceAndShow()
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Неверный запрос $response")
                    textViewWord1.text = "Ваше предложение: \n ${sentence.getStringSentence()}"
                }
            }
        }
    }

    private suspend fun waitGameOverGetSentenceAndShow() {
        withContext(Dispatchers.Default) {
            var isGameOver = false
            while (!isGameOver) {

                Log.d("checkGameOver", ".........checkGameOver")
                val response = client.isGameActive(gameId)
                if (response.isSuccessful && response.body() != null) {
                    isGameOver = !response.body()!!
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Игра не существует")
                        textViewWord1.text = "Ваше предложение: \n ${sentence.getStringSentence()}"
                    }
                    break
                }
                if (!isGameOver) {
                    getListUserNotSendSentenceAndUpdateListOrShowToast()
                    delay(500)
                }
            }
            if (isGameOver) {
                getSentenceAndShow()
                withContext(Dispatchers.Main) {
                    hideRecyclerView()
                    hideTextUsers()
                }
            }
        }
    }

    private suspend fun getListUserNotSendSentenceAndUpdateListOrShowToast() {
        withContext(Dispatchers.IO) {
            val response = client.getInfoIsUserSentSentence(gameId)
            if (response.isSuccessful && response.body() != null) {
                val usersSentSentenceInfo = response.body()!!
                val filterListUserNotSendSentence = usersSentSentenceInfo
                    .filter { !it.value }
                    .map { it.key }
                updateRecyclerView(filterListUserNotSendSentence)
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Неверный запрос $response")
                    textViewWord1.text = "Ваше предложение: \n ${sentence.getStringSentence()}"
                }
            }
        }
    }

    private suspend fun updateRecyclerView(listUsers: List<Int>) {
        withContext(Dispatchers.Main) {
            val adapter = ListUsersAdapter(listUsers, object : ListUsersAdapter.ButtonClickListener {
                override fun onButtonRemoveClick(id: Int) {
                    if (id != userId) {
                        deleteUser(id)
                    } else {
                        showToast("Нельзя удалить себя")
                    }
                }
            })
            recyclerView.adapter = adapter
        }
    }

    private fun deleteUser(userId: Int) {
        coroutineScope.launch(coroutineExceptionHandler) {
            showProgressBar()
            withContext(Dispatchers.IO) {
                val response = client.deleteUserFromGame(gameId, userId)
                if (response.isSuccessful) {
                    delay(500)
                    getListUserNotSendSentenceAndUpdateListOrShowToast()
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Неверный запрос $response")
                    }
                }
            }
            hideProgressBar()
        }
    }

    private suspend fun getSentenceAndShow() {
        withContext(Dispatchers.IO) {
            val response = client.getSentenceIfGameOver(gameId, userId)
            if (response.isSuccessful && response.body() != null) {
                val sentence = response.body()!!
                withContext(Dispatchers.Main) {
                    showSentence(sentence)
                    showButtonNewGame()
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Неверный запрос $response")
                    textViewWord1.text = "Ваше предложение: \n $sentence"
                }
            }
        }
    }

    private fun showSentence(sentence: List<String>) {
        textViewWord1.text = sentence[0].toUpperCase()
        textViewWord2.text = sentence[1].toUpperCase()
        textViewWord3.text = sentence[2].toUpperCase()
        textViewWord4.text = sentence[3].toUpperCase()
        textViewWord5.text = sentence[4].toUpperCase()
        textViewWord6.text = sentence[5].toUpperCase()
        textViewWord7.text = sentence[6].toUpperCase()
        textViewWord8.text = sentence[7].toUpperCase()
    }

    private fun startNewGame() {
        val intent: Intent =
            if (host) {
                Intent(this, HostActivity::class.java)
            } else {
                if (networkVersion) {
                    Intent(this, WaitingActivity::class.java)
                } else {
                    Intent(this, CreateSentenceActivity::class.java)
                }
            }
        startActivity(intent)
        finish()
    }

    private fun hideRecyclerView() {
        recyclerView.visibility = View.INVISIBLE
    }

    private fun hideTextUsers() {
        textViewUsers.visibility = View.INVISIBLE
    }

    private fun showButtonNewGame() {
        buttonNewGame.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()

        coroutineScope.coroutineContext.cancelChildren()
    }


}

