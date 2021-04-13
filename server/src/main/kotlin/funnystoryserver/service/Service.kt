package funnystoryserver.service

import org.springframework.stereotype.Service
import funnystoryserver.model.Game
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class Service {

    private val userGameHM = ConcurrentHashMap<Int, Game>()
    private val counterUserId = AtomicInteger()

    private val gamesHM = ConcurrentHashMap<Int, Game>()
    private val counterGameId = AtomicInteger()

    fun createNewGameAndReturnIndex(hostId: Int): Int {
        val newGameId = counterGameId.incrementAndGet()
        val game = Game(hostId)
        gamesHM.put(newGameId, game)
        return newGameId
    }

    fun getGameById(gameId: Int) = gamesHM[gameId]

    fun isGameExist(gameId: Int): Boolean {
        return gameId < gamesHM.size
    }

    fun getNewUserId(): Int = counterUserId.incrementAndGet()

    fun addNewPlayerInGame(gameId: Int, userId: Int) {
        val game = gamesHM[gameId]
        userGameHM.put(userId, game!!)
        game.addNewUserInGame(userId)
    }

    fun getCurrentGameByUserId(userId: Int) = userGameHM.get(userId)

}