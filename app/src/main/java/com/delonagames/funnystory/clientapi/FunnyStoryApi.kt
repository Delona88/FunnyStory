package com.delonagames.funnystory.clientapi

import retrofit2.Response
import retrofit2.http.*

interface FunnyStoryApi {

    @GET("/users/userId")
    suspend fun getNewUserId(): Response<Int>

    @POST("/games/{hostId}")
    suspend fun createNewGameAddHostAndGetGameId(
        @Path("hostId") hostId: Int
    ): Response<Int>

    @GET("/games/{gameId}/users")
    suspend fun getAllUsersByGameId(
        @Path("gameId") gameId: Int
    ): Response<List<Int>>

    @PUT("/games/{gameId}/active")
    suspend fun setGameActive(
        @Path("gameId") gameId: Int,
        @Query("active") active: Boolean
    ): Response<Unit>

    @GET("/games/{gameId}/active")
    suspend fun isGameActive(
        @Path("gameId") gameId: Int
    ): Response<Boolean>

    @POST("/games/{gameId}/users/{userId}")
    suspend fun addUserInGame(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int
    ): Response<Unit>

    @PUT("/games/{gameId}/user/{userId}/sentence")
    suspend fun setSentence(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int,
        @Body sentence: List<String>
    ): Response<Unit>

    @GET("/games/{gameId}/user/{userId}/sentence")
    suspend fun getSentenceIfGameOver(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int
    ): Response<List<String>>

    @GET("/games/{gameId}/isUserSentSentence")
    suspend fun getInfoIsUserSentSentence(
        @Path("gameId") gameId: Int
    ): Response<Map<Int, Boolean>>

    @DELETE("/games/{gameId}/user/{userId}")
    suspend fun deleteUserFromGame(
        @Path("gameId") gameId: Int,
        @Path("userId") userId: Int
    ): Response<Unit>

}