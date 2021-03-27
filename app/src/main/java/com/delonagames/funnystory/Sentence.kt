package com.delonagames.funnystory

class Sentence {
    private val words: MutableList<String> = mutableListOf()

    fun addWord(word: String) {
        words.add(word)
    }

    fun clearSentence() {
        words.clear()
    }

    fun getSentence(): String {
        return words.reduce { str, element -> str + element }
    }
}