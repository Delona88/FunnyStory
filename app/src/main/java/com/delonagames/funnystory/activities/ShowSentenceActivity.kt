package com.delonagames.funnystory.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.model.Sentence
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity.Sentence.sentence
import com.delonagames.funnystory.activities.host.HostActivity
import com.delonagames.funnystory.clientapi.RetrofitInterfaceApi
import kotlinx.coroutines.*
import java.net.ConnectException

class ShowSentenceActivity : AppCompatActivity() {

    private var coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var buttonNewGame: Button

    private lateinit var client: RetrofitInterfaceApi

    private var networkVersion: Boolean = false
    private var gameId = 0
    private var userId = 0
    private var host = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_sentence)

        val funnyStoryApp = applicationContext as FunnyStoryApp
        client = funnyStoryApp.client
        networkVersion = funnyStoryApp.networkVersion
        gameId = funnyStoryApp.gameId
        userId = funnyStoryApp.userId
        host = funnyStoryApp.host


        textView = findViewById(R.id.textView)

        progressBar = findViewById(R.id.progressBar)

        buttonNewGame = findViewById(R.id.buttonNew)
        buttonNewGame.visibility = View.INVISIBLE

        buttonNewGame.setOnClickListener {
            goStart()
        }

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
            val msg = when (t) {
                is ConnectException -> "Проблемы с сервером. "
                else -> "Что-то пошло не так."
            }
            t.printStackTrace()
            showToast("$msg $t")
            coroutineScope.coroutineContext.cancelChildren()
            finish()
        }

        if (networkVersion) {
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                sendSentence()
                getAndShowSentence()
                showButton()
                hideProgressBar()
            }
        } else {
            textView.text = sentence.getStringSentence()
            showButton()
        }
    }


    private suspend fun sendSentence() {
        withContext(Dispatchers.IO) {
            val response = client.sendSentence(gameId, userId, sentence.toListOfStrings())
            if (!response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    showToastServerProblem()
                }
            }
        }
    }

    private suspend fun getAndShowSentence() {
        withContext(Dispatchers.IO) {
            tryGetSentence()
            getSentenceAndShow()
        }
    }

    private suspend fun tryGetSentence() {
        withContext(Dispatchers.Default) {
            var isGameOver = false
            while (!isGameOver) {
                val response = client.isGameOver(gameId)
                if (response.isSuccessful && response.body() != null) {
                    isGameOver = response.body()!!
                } else {
                    isGameOver = true
                    withContext(Dispatchers.Main) {
                        showToastServerProblem()
                    }
                }
                delay(500)
            }
        }
    }

    private suspend fun getSentenceAndShow() {
        withContext(Dispatchers.Default) {
            val response = client.getSentence(gameId, userId)
            if (response.isSuccessful && response.body() != null) {
                val sentence = Sentence(response.body()!!.toMutableList())
                setText(sentence.getStringSentence())
            } else {
                withContext(Dispatchers.Main) {
                    showToastServerProblem()
                }
            }
        }
    }

    private suspend fun setText(text: String) {
        withContext(Dispatchers.Main) {
            textView.text = text
        }
    }

    private fun goStart() {
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

    private fun showButton() {
        buttonNewGame.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showToastServerProblem() {
        Toast.makeText(this, "Проблемы с сервером", Toast.LENGTH_LONG).show()
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineScope.coroutineContext.cancelChildren()
    }
}


