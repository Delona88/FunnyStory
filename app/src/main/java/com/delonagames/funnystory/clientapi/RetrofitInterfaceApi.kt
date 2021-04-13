package com.delonagames.funnystory.clientapi

import retrofit2.Response
import retrofit2.http.*

interface RetrofitInterfaceApi {

    @GET("/users/userId")
    suspend fun getNewUserId(): Response<Int>

    @POST("/games/{hostId}")
    suspend fun createNewGameAddHostAndGetGameId(
        @Path("hostId") hostId: Int
    ): Response<Int>


    @GET("/games/{gameId}/users")
    suspend fun getAllUsersByGameId(@Path("gameId") gameId: Int): Response<List<Int>>

    @PUT("/games/{gameId}/active")
    suspend fun setGameActiveTrue(@Path("gameId") gameId: Int): Response<Unit>

    @GET("/games/{gameId}/active")
    suspend fun isGameActive(@Path("gameId") gameId: Int): Response<Boolean>

    @POST("/games/{gameId}/users/{userId}")
    suspend fun connectUserToGame(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int
    ): Response<Unit>

    @PUT("/games/{gameId}/user/{userId}/sentence")
    suspend fun sendSentence(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int,
        @Body sentence: List<String>
    ): Response<Unit>

    @GET("/games/{gameId}/gameOver")
    suspend fun isGameOver(@Path("gameId") gameId: Int): Response<Boolean>

    @GET("/games/{gameId}/user/{userId}/sentence")
    suspend fun getSentenceIfGameOver(@Path("gameId") gameId: Int, @Path("userId") userId: Int): Response<List<String>>

    @PUT("/games/{gameId}")
    suspend fun endGameNow(
        @Path("gameId") gameId: Int
    ): Response<Unit>

    @GET("/games/{gameId}/userSentences")
    suspend fun getInfoIsUserSentSentence(
        @Path("gameId") gameId: Int
    ): Response<Map<Int, Boolean>>

    @DELETE("/games/{gameId}/user/{userId}/sentence")
    suspend fun disconnectUserIfSentenceNotSent(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int
    ): Response<Unit>

    @DELETE("/games/{gameId}/user/{userId}")
    suspend fun disconnectUser(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int
    ): Response<Unit>

}