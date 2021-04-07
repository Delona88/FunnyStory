package com.delonagames.funnystory.activities.host

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity
import com.delonagames.funnystory.clientapi.RetrofitInterfaceApi
import kotlinx.coroutines.*
import java.net.ConnectException

class HostActivity : AppCompatActivity() {

    private var coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private lateinit var buttonStart: Button
    private lateinit var buttonUpdate: Button
    private lateinit var textViewGameId: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var client: RetrofitInterfaceApi

    private lateinit var funnyStoryApp: FunnyStoryApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        funnyStoryApp = applicationContext as FunnyStoryApp
        client = funnyStoryApp.client

        textViewGameId = findViewById(R.id.textViewGameId)
        textViewGameId.text = funnyStoryApp.gameId.toString()

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        progressBar = findViewById(R.id.progressBar)

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

        buttonStart = findViewById(R.id.buttonStart)
        buttonStart.setOnClickListener {
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                setGameActive()
                hideProgressBar()
                goToCreateSentenceActivity()
            }
        }

        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonUpdate.setOnClickListener {
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                getAllUsersByGameIdAndUpdateList()
                hideProgressBar()
            }
        }

        coroutineScope.launch(coroutineExceptionHandler) {
            showProgressBar()
            getAllUsersByGameIdAndUpdateList()
            hideProgressBar()
        }
    }

    private suspend fun setGameActive() {
        withContext(Dispatchers.IO) {
            client.setGameActive(funnyStoryApp.gameId, true)
        }
    }

    private suspend fun getAllUsersByGameIdAndUpdateList() {
        withContext(Dispatchers.IO) {
            val response = client.getAllUsersByGameId(funnyStoryApp.gameId)
            if (response.isSuccessful && response.body() != null) {
                updateList(response.body()!!)
            } else {
                showToastServerProblem()
            }
        }
    }

    private suspend fun updateList(listUsers: List<Int>) {
        withContext(Dispatchers.Main) {
            val adapter = ListUsersAdapter(listUsers)
            recyclerView.adapter = adapter
        }
    }

    private fun goToCreateSentenceActivity() {
        intent = Intent(this, CreateSentenceActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showToastServerProblem() {
        Toast.makeText(this, "Проблемы с сервером. Попробуйте позже", Toast.LENGTH_LONG).show()
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineScope.coroutineContext.cancelChildren()
    }

}







