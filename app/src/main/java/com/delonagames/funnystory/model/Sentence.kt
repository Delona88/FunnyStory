package com.delonagames.funnystory.model

class Sentence {
    private var words: MutableList<String> = mutableListOf()
    private val questions = mutableListOf(
        "Когда?",
        "Какой?",
        "Кто?",
        "Что делает?",
        "Где?",
        "С кем?",
        "Для чего?",
        "Чем дело закончилось?"
    )

    constructor(words: MutableList<String>) {
        this.words = words
    }

    constructor()

    fun addWord(word: String) {
        words.add(word)
    }

    fun clearSentence() {
        words.clear()
    }

    fun getStringSentence(): String {
        return words.reduce { str, element -> str + element }
    }

    fun getQuestion(index: Int): String {
        return questions[index]
    }

    fun getNumberOfQuestions(): Int{
        return questions.size
    }

    fun toListOfStrings(): MutableList<String> {
        return words
    }
}