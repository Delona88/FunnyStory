package funnystoryserver.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import funnystoryserver.service.Service

@RestController
class FunnyStoryApiController(@Autowired val service: Service) {

    @GetMapping("/games/gameId")
    fun getGameId(): ResponseEntity<Int> {
        return ResponseEntity(service.createNewGameAndReturnIndex(), HttpStatus.OK)
    }

    @GetMapping("/games/{gameId}/users")
    fun getAllUsersByGameId(@PathVariable(name = "gameId") gameId: Int): ResponseEntity<List<Int>> {
        return ResponseEntity(service.getGameById(gameId).getIdAllUsers().toList(), HttpStatus.OK)
    }

    @PostMapping("/games/{gameId}/active")
    fun setGameActive(
        @PathVariable(name = "gameId") gameId: Int,
        @RequestParam(value = "active") active: Boolean
    ): ResponseEntity<Unit> {
        service.getGameById(gameId).setGameActiveTrue()
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/games/{gameId}/active")
    fun isGameActive(@PathVariable(name = "gameId") gameId: Int): ResponseEntity<Boolean> {
        return ResponseEntity(service.getGameById(gameId).isGameActive(), HttpStatus.OK)
    }

    @GetMapping("/games/{gameId}/users/id")
    fun connectToGameAndGetUserId(@PathVariable(name = "gameId") gameId: Int): ResponseEntity<Int> {
        return if (service.isGameExist(gameId) && !service.getGameById(gameId).isGameActive()) {
            ResponseEntity(service.getGameById(gameId).getNewUserId(), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/games/{gameId}/user/{userId}/sentence")
    fun sendSentence(
        @PathVariable(name = "gameId") gameId: Int, @PathVariable(name = "userId") userId: Int,
        @RequestBody sentence: List<String>
    ): ResponseEntity<Unit> {
        service.getGameById(gameId).setSentenceByUserId(userId, sentence.toMutableList())
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/games/{gameId}/gameOver")
    fun isGameOver(@PathVariable(name = "gameId") gameId: Int): ResponseEntity<Boolean> {
        return ResponseEntity(!service.getGameById(gameId).isGameActive(), HttpStatus.OK)
    }

    @GetMapping("/games/{gameId}/user/{userId}/sentence")
    fun getSentence(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<List<String>> {
        return if (!service.getGameById(gameId).isGameActive()) {
            ResponseEntity(service.getGameById(gameId).getNewSentenceByUserId(userId), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/games/{gameId}/user/{userId}")
    fun disconnect(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<Unit> {
        service.getGameById(gameId).removeUser(userId)
        return ResponseEntity(HttpStatus.OK)
    }
}