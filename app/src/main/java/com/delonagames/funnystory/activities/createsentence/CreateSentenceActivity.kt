package com.delonagames.funnystory.activities.createsentence

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.delonagames.funnystory.*
import com.delonagames.funnystory.activities.ShowSentenceActivity
import com.delonagames.funnystory.activities.createsentence.CreateSentenceActivity.Sentence.sentence
import com.delonagames.funnystory.model.Sentence
import kotlinx.coroutines.cancelChildren

class CreateSentenceActivity : AppCompatActivity(),
    CreateSentenceActivityInterface {

    private lateinit var currentFragment: Fragment

    object Sentence {
        val sentence = Sentence()
    }

    private var questionNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_sentence)

        if (savedInstanceState != null) {
            questionNumber = savedInstanceState.getInt("questionNumber")
        } else {
            sentence.clearSentence()
        }
        addFragmentWithText()
    }

    private fun addFragmentWithText() {
        currentFragment = OneWordFragment.newInstance(
            sentence.getQuestion(questionNumber)
        )

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, currentFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun goNext(word: String) {

        sentence.addWord(word)
        questionNumber++
        if (questionNumber < sentence.getNumberOfQuestions()) {
            addFragmentWithText()
        } else {
            supportFragmentManager.beginTransaction().remove(currentFragment).commit()
            goToShowSentenceActivity()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("questionNumber", questionNumber)
    }

    private fun goToShowSentenceActivity() {
        intent = Intent(this, ShowSentenceActivity::class.java)
        startActivity(intent)
        finish()
    }


}