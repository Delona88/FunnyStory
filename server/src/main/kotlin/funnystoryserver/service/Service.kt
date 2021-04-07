package funnystoryserver.service

import org.springframework.stereotype.Service
import funnystoryserver.model.Game

@Service
class Service {

    private val listGames: MutableList<Game> = mutableListOf()

    fun createNewGameAndReturnIndex(): Int {
        val game = Game()
        listGames.add(game)
        return listGames.indexOf(game)
    }

    fun getGameById(gameId: Int) = listGames[gameId]

    fun isGameExist(gameId: Int): Boolean {
        return gameId < listGames.size
    }

}