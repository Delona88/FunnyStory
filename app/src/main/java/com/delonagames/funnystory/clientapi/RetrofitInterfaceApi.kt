package com.delonagames.funnystory.clientapi

import retrofit2.Response
import retrofit2.http.*

interface RetrofitInterfaceApi {
    @GET("/games/gameId")
    suspend fun getGameId(): Response<Int>

    @GET("/games/{gameId}/users")
    suspend fun getAllUsersByGameId(@Path("gameId") gameId: Int): Response<List<Int>>

    @POST("/games/{gameId}/active")
    suspend fun setGameActive(@Path("gameId") gameId: Int, @Query("active") active: Boolean)

    @GET("/games/{gameId}/active")
    suspend fun isGameActive(@Path("gameId") gameId: Int): Response<Boolean>

    @GET("/games/{gameId}/users/id")
    suspend fun connectToGameAndGetUserId(@Path("gameId") gameId: Int): Response<Int>

    @POST("/games/{gameId}/user/{userId}/sentence")
    suspend fun sendSentence(@Path("gameId") gameId: Int, @Path("userId") userId: Int, @Body sentence: List<String>): Response<Unit>

    @GET("/games/{gameId}/gameOver")
    suspend fun isGameOver(@Path("gameId") gameId: Int): Response<Boolean>

    @GET("/games/{gameId}/user/{userId}/sentence")
    suspend fun getSentence(@Path("gameId") gameId: Int, @Path("userId") userId: Int): Response<List<String>>

    @DELETE("/games/{gameId}/user/{userId}")
    suspend fun disconnect(@Path("gameId") gameId: Int, @Path("userId") userId: Int)

}