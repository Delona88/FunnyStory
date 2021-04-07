package funnystoryserver.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class Game {
    private var userSentences = ConcurrentHashMap<Int, MutableList<String>>()
    private var newUserSentences = ConcurrentHashMap<Int, MutableList<String>>()
    private var isUserSentSentence = ConcurrentHashMap<Int, Boolean>()
    private var active = false
    private var sentSentenceCounter = AtomicInteger()

    init {
        userSentences[0] = mutableListOf()
        isUserSentSentence[0] = false
        println("хост индекс - ${userSentences.size - 1}")
    }

    @Synchronized fun getNewUserId(): Int {
        val newIndex = userSentences.size
        userSentences[newIndex] = mutableListOf()
        isUserSentSentence[newIndex] = false
        println("новый user индекс - $newIndex")
        return newIndex
    }

    fun getIdAllUsers() = userSentences.keys

    fun setGameActiveTrue() {
        this.active = true
        startNewGame()
    }

    fun isGameActive() = active

    fun setSentenceByUserId(userId: Int, sentence: MutableList<String>) {
        if (!isUserSentSentence[userId]!!){
            sentSentenceCounter.incrementAndGet()
            userSentences[userId] = sentence
            isUserSentSentence[userId] = true
        }

        if (sentSentenceCounter.get() == userSentences.size) {
            mixSentence()
            active = false
        }
    }

    private fun mixSentence() {

        val numberOfUser = userSentences.size
        val numberOfWord = userSentences[0]!!.size

        for (sentenceNumber in 0 until numberOfUser) {
            val sentence = mutableListOf<String>()
            for (wordNumber in 0 until numberOfWord) {
                val newSentenceNumber = (sentenceNumber + wordNumber) % numberOfUser
                val newSentence = userSentences[newSentenceNumber]!!
                val newWord = newSentence[wordNumber]

                sentence.add(newWord)
            }
            newUserSentences[sentenceNumber] = sentence
        }
    }

    private fun startNewGame() {
        sentSentenceCounter.set(0)
        for (user in isUserSentSentence.keys){
            isUserSentSentence[user] = false
        }
    }

    fun getNewSentenceByUserId(userId: Int): List<String> = newUserSentences[userId] ?: mutableListOf()

    //TODO removeUser not implemented in app
    fun removeUser(userId: Int) {
        userSentences.remove(userId)
    }

    override fun toString(): String {
        return "Game(usersSentence=$userSentences)"
    }

}