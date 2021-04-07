package com.delonagames.funnystory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.delonagames.funnystory.FunnyStoryApp
import com.delonagames.funnystory.R
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity
import kotlinx.coroutines.cancelChildren

class MainSelectVersionActivity : AppCompatActivity() {
    private lateinit var buttonLocal: Button
    private lateinit var buttonNetwork: Button

    private lateinit var funnyStoryApp: FunnyStoryApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_select_version)

        funnyStoryApp = applicationContext as FunnyStoryApp

        buttonLocal = findViewById(R.id.buttonLocal)
        buttonLocal.setOnClickListener {
            goToLocal()
        }

        buttonNetwork = findViewById(R.id.buttonNetwork)
        buttonNetwork.setOnClickListener {
            goToNetwork()
        }
    }

    private fun goToNetwork() {
        intent = Intent(this, StartNewGameOrConnectActivity::class.java)
        funnyStoryApp.networkVersion = true
        startActivity(intent)
        //finish()
    }

    private fun goToLocal() {
        intent = Intent(this, CreateSentenceActivity::class.java)
        funnyStoryApp.networkVersion = false
        startActivity(intent)
        //finish()
    }

}