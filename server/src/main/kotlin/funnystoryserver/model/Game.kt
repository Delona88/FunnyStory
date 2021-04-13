package funnystoryserver.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

class Game(val hostId: Int) {
    private var usersId = ArrayList<Int>()
    private var userSentences = ConcurrentHashMap<Int, MutableList<String>>()
    private var newUserSentences = ConcurrentHashMap<Int, MutableList<String>>()
    private var isUserSentSentence = ConcurrentHashMap<Int, Boolean>()
    private var active = false
    private var sentSentenceCounter = AtomicInteger()

    init {
        usersId.add(hostId)
        println("добавлен host $hostId")
    }

    @Synchronized
    fun addNewUserInGame(userId: Int) {
        if (!usersId.contains(userId)) {
            usersId.add(userId)
        }
        println("добавлен user $userId")
    }

    fun getIdAllUsers() = usersId

    fun setGameActiveTrue() {
        this.active = true
        startNewGame()
        println("usersId $usersId")
    }

    fun isGameActive() = active

    fun setSentenceByUserId(userId: Int, sentence: MutableList<String>) {
        if (userSentences[userId] == null) {
            sentSentenceCounter.incrementAndGet()
            userSentences[userId] = sentence
        }

        if (sentSentenceCounter.get() == usersId.size) {
            endGame()
        }
    }

    fun isUserInGame(userId: Int) = usersId.contains(userId)

    private fun endGame() {
        println("endGame : usersId.size - ${usersId.size} sentSentenceCounter - ${sentSentenceCounter.get()}")
        mixSentence()
        active = false

    }

    private fun mixSentence() {

        val numberOfUser = usersId.size
        val numberOfWord = userSentences[hostId]!!.size

        for (sentenceNumber in 0 until numberOfUser) {
            val sentence = mutableListOf<String>()

            for (wordNumber in 0 until numberOfWord) {
                val newSentenceNumber = (sentenceNumber + wordNumber) % numberOfUser
                val newSentence = userSentences[usersId.get(newSentenceNumber)]!!
                val newWord = newSentence[wordNumber]

                sentence.add(newWord)
            }
            newUserSentences.set(usersId.get(sentenceNumber), sentence)
        }

    }

    private fun startNewGame() {
        sentSentenceCounter.set(0)
        userSentences = ConcurrentHashMap()
    }

    fun getNewSentenceByUserId(userId: Int): List<String>? = newUserSentences[userId] //?: mutableListOf()

    //завершение игры хостом не дожидаясь отправки
    fun endGameNow() {
        usersId = ArrayList(userSentences.keys)
        endGame()
    }

    fun getInfoIsUserSentSentence(): ConcurrentHashMap<Int, Boolean> {
        val isUserSentSentenceHM = ConcurrentHashMap<Int, Boolean>()
        for (userId in usersId) {
            if (userSentences[userId] != null) {
                isUserSentSentenceHM.put(userId, true)
            } else {
                isUserSentSentenceHM.put(userId, false)
            }
        }
        return isUserSentSentenceHM
    }

    fun removeUserIfSentenceNotSent(userId: Int) {
        if (userSentences[userId] == null) {
            usersId.remove(userId)
            println("удален user $userId")
            println("removeUserIfSentenceNotSent уделен usersId.size - ${usersId.size} sentSentenceCounter - ${sentSentenceCounter.get()}")
        }

        if (sentSentenceCounter.get() == usersId.size) {
            endGame()
            println("removeUserIfSentenceNotSent : usersId.size - ${usersId.size} sentSentenceCounter - ${sentSentenceCounter.get()}")
        }
    }

    fun removeUser(userId: Int) {
        usersId.remove(userId)
        println("удален user $userId")
        println("removeUser уделен usersId.size - ${usersId.size} sentSentenceCounter - ${sentSentenceCounter.get()}")

    }


    override fun toString(): String {
        return "Game(usersSentence=$userSentences)"
    }

}