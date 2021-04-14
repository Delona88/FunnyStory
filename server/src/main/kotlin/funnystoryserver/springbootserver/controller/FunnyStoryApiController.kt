package funnystoryserver.springbootserver.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import funnystoryserver.springbootserver.service.Service

@RestController
class FunnyStoryApiController(@Autowired val service: Service) {

    @GetMapping("/users/userId")
    fun getNewUserId(): ResponseEntity<Int> {
        return ResponseEntity(service.getNewUserId(), HttpStatus.OK)
    }

    @PostMapping("/games/{hostId}")
    fun createNewGameAddHostAndGetGameId(
        @PathVariable(name = "hostId") hostId: Int
    ): ResponseEntity<Int> {
        return ResponseEntity(service.createNewGameAndReturnId(hostId), HttpStatus.OK)
    }

    @PostMapping("/games/{gameId}/users/{userId}")
    fun addUserInGame(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null && !game.isGameActive()) {
            game.addUserInGame(userId)
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/games/{gameId}/users")
    fun getAllUsersByGameId(@PathVariable(name = "gameId") gameId: Int): ResponseEntity<List<Int>> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            ResponseEntity(game.getIdAllUsers().toList(), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/games/{gameId}/active")
    fun setGameActive(
        @PathVariable(name = "gameId") gameId: Int,
        @RequestParam(name = "active") active: Boolean
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null && active) {
            game.setGameActiveTrue()
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/games/{gameId}/active")
    fun isGameActive(@PathVariable(name = "gameId") gameId: Int): ResponseEntity<Boolean> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            ResponseEntity(game.isGameActive(), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/games/{gameId}/user/{userId}/sentence")
    fun setSentence(
        @PathVariable(name = "gameId") gameId: Int, @PathVariable(name = "userId") userId: Int,
        @RequestBody sentence: List<String>
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null && game.isUserInGame(userId)) {
            game.setSentenceByUserId(userId, sentence.toMutableList())
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/games/{gameId}/user/{userId}/sentence")
    fun getSentenceIfGameOver(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<List<String>> {
        val game = service.getGameById(gameId)
        if (game != null && !game.isGameActive()) {
            val sentence = game.getNewSentenceByUserId(userId)
            if (sentence != null) {
                return ResponseEntity(sentence, HttpStatus.OK)
            }
        }
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/games/{gameId}/isUserSentSentence")
    fun getInfoIsUserSentSentence(
        @PathVariable(name = "gameId") gameId: Int
    ): ResponseEntity<Map<Int, Boolean>> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            ResponseEntity(game.getInfoIsUserSentSentence(), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/games/{gameId}/user/{userId}")
    fun deleteUserFromGame(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            game.deleteUserIfSentenceNotSent(userId)
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}