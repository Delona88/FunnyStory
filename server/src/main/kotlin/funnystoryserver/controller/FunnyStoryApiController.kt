package funnystoryserver.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import funnystoryserver.service.Service

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
        return ResponseEntity(service.createNewGameAndReturnIndex(hostId), HttpStatus.OK)
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
    fun setGameActiveTrue(
        @PathVariable(name = "gameId") gameId: Int
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null) {
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

    @PostMapping("/games/{gameId}/users/{userId}")
    fun connectUserToGame(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null && !game.isGameActive()) {
            service.addNewPlayerInGame(gameId, userId)
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }


    @PutMapping("/games/{gameId}/user/{userId}/sentence")
    fun sendSentence(
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

    @GetMapping("/games/{gameId}/gameOver")
    fun isGameOver(@PathVariable(name = "gameId") gameId: Int): ResponseEntity<Boolean> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            ResponseEntity(!game.isGameActive(), HttpStatus.OK)
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

    @PutMapping("/games/{gameId}")
    fun endGameNow(
        @PathVariable(name = "gameId") gameId: Int
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            game.endGameNow()
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/games/{gameId}/userSentences")
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

    @DeleteMapping("/games/{gameId}/user/{userId}/sentence")
    fun disconnectUserIfSentenceNotSent(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            game.removeUserIfSentenceNotSent(userId)
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/games/{gameId}/user/{userId}")
    fun disconnectUser(
        @PathVariable(name = "gameId") gameId: Int,
        @PathVariable(name = "userId") userId: Int
    ): ResponseEntity<Unit> {
        val game = service.getGameById(gameId)
        return if (game != null) {
            game.removeUser(userId)
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}