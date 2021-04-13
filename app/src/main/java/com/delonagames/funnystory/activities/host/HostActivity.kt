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
import com.delonagames.funnystory.clientapi.NetworkService
import kotlinx.coroutines.*
import java.net.ConnectException

class HostActivity : AppCompatActivity() {

    private lateinit var buttonStart: Button
    private lateinit var buttonUpdate: Button
    private lateinit var textViewGameId: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var funnyStoryApp: FunnyStoryApp
    private var gameId = 0
    private var userId = 0

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
        setContentView(R.layout.activity_host)

        initData()
        buildGUI()
    }

    private fun initData() {
        funnyStoryApp = applicationContext as FunnyStoryApp
        gameId = funnyStoryApp.gameId
        userId = funnyStoryApp.userId
    }

    private fun buildGUI() {
        textViewGameId = findViewById(R.id.textViewGameId)
        textViewGameId.text = funnyStoryApp.gameId.toString()

        progressBar = findViewById(R.id.progressBar)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        getUsersIdAndUpdateRecyclerView()

        buttonStart = findViewById(R.id.buttonStart)
        buttonStart.setOnClickListener {
            coroutineScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                setGameActiveTrueAndStartCreateSentenceActivity()
                hideProgressBar()
            }
        }

        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonUpdate.setOnClickListener {
            getUsersIdAndUpdateRecyclerView()
        }

    }

    private fun getUsersIdAndUpdateRecyclerView() {
        coroutineScope.launch(coroutineExceptionHandler) {
            showProgressBar()
            getAllUsersIdAndUpdateListOrShowToast()
            hideProgressBar()
        }
    }

    private suspend fun getAllUsersIdAndUpdateListOrShowToast() {
        withContext(Dispatchers.IO) {
            val response = client.getAllUsersByGameId(gameId)
            if (response.isSuccessful && response.body() != null) {
                updateRecyclerView(response.body()!!)
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Неверный запрос $response")
                }
            }
        }
    }

    private suspend fun updateRecyclerView(listUsersId: List<Int>) {
        withContext(Dispatchers.Main) {
            val adapter = ListUsersAdapter(listUsersId, object : ListUsersAdapter.ButtonClickListener {
                override fun onButtonRemoveClick(id: Int) {
                    if (id != userId) {
                        deleteUser(id)
                    } else {
                        showToast("Невозможно удалить себя")
                    }
                }
            })
            recyclerView.adapter = adapter
        }
    }

    private fun deleteUser(userId: Int) {
        coroutineScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = client.disconnectUser(gameId, userId)
            if (response.isSuccessful) {
                getAllUsersIdAndUpdateListOrShowToast()
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Неверный запрос $response")
                }
            }
        }
    }

    private suspend fun setGameActiveTrueAndStartCreateSentenceActivity() {
        withContext(Dispatchers.IO) {
            val response = client.setGameActiveTrue(gameId)
            if (response.isSuccessful) {
                goToCreateSentenceActivity()
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Неверный запрос $response")
                }
            }
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

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineScope.coroutineContext.cancelChildren()
    }

}







