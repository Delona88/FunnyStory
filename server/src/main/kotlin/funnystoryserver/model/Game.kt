package funnystoryserver.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

class Game(val hostId: Int) {
    private var usersId = ArrayList<Int>()
    private var userSentences = ConcurrentHashMap<Int, MutableList<String>>()
    private var newUserSentences = ConcurrentHashMap<Int, MutableList<String>>()
    private var active = false
    private var sentSentenceCounter = AtomicInteger()

    init {
        usersId.add(hostId)
        println("InitGame hostId - $hostId")
    }

    @Synchronized
    fun addUserInGame(userId: Int) {
        if (!usersId.contains(userId)) {
            usersId.add(userId)
        }
        println("AddNewUserInGame id - $userId")
    }

    fun getIdAllUsers() = usersId

    fun setGameActiveTrue() {
        this.active = true
        println("SetGameActiveTrue usersId - $usersId")
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
        mixSentence()
        active = false
        sentSentenceCounter.set(0)
        userSentences = ConcurrentHashMap()
        println("EndGame : usersId.size - ${usersId.size} sentSentenceCounter - ${sentSentenceCounter.get()}")
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

    fun getNewSentenceByUserId(userId: Int): List<String>? = newUserSentences[userId] //?: mutableListOf()

    //завершение игры хостом не дожидаясь отправки
    fun endGameNow() {
        usersId = ArrayList(userSentences.keys)
        endGame()
    }

    fun getInfoIsUserSentSentence(): ConcurrentHashMap<Int, Boolean> {
        //TODO проверить
        val isUserSentSentenceHM = usersId.associateWith { userSentences[it] != null }
        return ConcurrentHashMap(isUserSentSentenceHM)

    }

    fun deleteUserIfSentenceNotSent(userId: Int) {
        if (userSentences[userId] == null) {
            usersId.remove(userId)
            println("RemoveUserIfSentenceNotSent id - $userId")
        }

        if (sentSentenceCounter.get() == usersId.size) endGame()
    }


    override fun toString(): String {
        return "Game(usersSentence=$userSentences)"
    }

}