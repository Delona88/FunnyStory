package com.delonagames.funnystory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.delonagames.funnystory.MainActivity.Sentence.sentence

class MainActivity : AppCompatActivity(), MainActivityInterface {

    object Sentence {
        val sentence = Sentence()
    }

    private var questionNumber = 0

    private val listWithQuestions = mutableListOf(
        "Какой?",
        "Кто?",
        "Что делает?",
        "Где?",
        "С кем?",
        "Когда?",
        "Для чего?",
        "Чем дело закончилось?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            questionNumber = savedInstanceState.getInt("questionNumber")
        } else {
            sentence.clearSentence()
        }

        addFragmentWithText()
    }

    private fun addFragmentWithText() {
        val fragment = OneWordFragment.newInstance(listWithQuestions[questionNumber])

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun goNext(word: String) {
        sentence.addWord(word)
        questionNumber++
        if (questionNumber < listWithQuestions.size) {
            addFragmentWithText()
        } else {
            intent = Intent(this, ShowSentenceActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("questionNumber", questionNumber)
    }

}