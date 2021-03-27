package com.delonagames.funnystory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.delonagames.funnystory.MainActivity.Sentence.sentence


class ShowSentenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_sentence)

        val textView: TextView = findViewById(R.id.textView)
        textView.text = sentence.getSentence()

        val buttonNew: Button = findViewById(R.id.buttonNew)
        buttonNew.setOnClickListener {
            when (it.id) {
                buttonNew.id -> goStart()
            }
        }
    }

    private fun goStart(){
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


