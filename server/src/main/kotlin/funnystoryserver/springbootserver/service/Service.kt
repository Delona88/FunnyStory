package funnystoryserver.springbootserver.service

import org.springframework.stereotype.Service
import funnystoryserver.model.Game
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class Service {

    private val counterUserId = AtomicInteger()

    private val gamesHM = ConcurrentHashMap<Int, Game>()
    private val counterGameId = AtomicInteger()

    fun getNewUserId(): Int = counterUserId.incrementAndGet()

    fun createNewGameAndReturnId(hostId: Int): Int {
        val newGameId = counterGameId.incrementAndGet()
        val game = Game(hostId)
        gamesHM.put(newGameId, game)
        return newGameId
    }

    fun getGameById(gameId: Int) = gamesHM[gameId]

}