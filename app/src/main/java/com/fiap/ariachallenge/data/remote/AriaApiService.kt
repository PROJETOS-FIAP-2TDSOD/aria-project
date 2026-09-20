package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.remote.dto.AuthResponseDto
import com.fiap.ariachallenge.data.remote.dto.IdeaDto
import com.fiap.ariachallenge.data.remote.dto.IdeaRequestDto
import com.fiap.ariachallenge.data.remote.dto.IdeaReviewDto
import com.fiap.ariachallenge.data.remote.dto.LoginRequestDto
import com.fiap.ariachallenge.data.remote.dto.MessageResponseDto
import com.fiap.ariachallenge.data.remote.dto.OrientationDto
import com.fiap.ariachallenge.data.remote.dto.ProjectDto
import com.fiap.ariachallenge.data.remote.dto.RecoverPasswordRequestDto
import com.fiap.ariachallenge.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AriaApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("api/v1/auth/recover-password")
    suspend fun recoverPassword(@Body request: RecoverPasswordRequestDto): MessageResponseDto

    @GET("api/v1/ideas")
    suspend fun getIdeas(): List<IdeaDto>

    @GET("api/v1/ideas/{id}")
    suspend fun getIdeaById(@Path("id") id: String): IdeaDto

    @POST("api/v1/ideas")
    suspend fun createIdea(@Body idea: IdeaRequestDto): IdeaDto

    @PUT("api/v1/ideas/{id}")
    suspend fun updateIdea(@Path("id") id: String, @Body idea: IdeaRequestDto): IdeaDto

    @PATCH("api/v1/ideas/{id}/review")
    suspend fun reviewIdea(@Path("id") id: String, @Body review: IdeaReviewDto): IdeaDto

    @POST("api/v1/ideas/{id}/ai-score")
    suspend fun scoreIdeaWithAi(@Path("id") id: String): IdeaDto

    @DELETE("api/v1/ideas/{id}")
    suspend fun deleteIdea(@Path("id") id: String)

    @GET("api/v1/projects")
    suspend fun getProjects(): List<ProjectDto>

    @POST("api/v1/projects")
    suspend fun createProject(@Body project: ProjectDto): ProjectDto

    @PUT("api/v1/projects/{id}")
    suspend fun updateProject(@Path("id") id: String, @Body project: ProjectDto): ProjectDto

    @DELETE("api/v1/projects/{id}")
    suspend fun deleteProject(@Path("id") id: String)

    @GET("api/v1/orientations")
    suspend fun getOrientations(): List<OrientationDto>

    @POST("api/v1/orientations")
    suspend fun createOrientation(@Body orientation: OrientationDto): OrientationDto

    @PUT("api/v1/orientations/{id}")
    suspend fun updateOrientation(@Path("id") id: String, @Body orientation: OrientationDto): OrientationDto

    @DELETE("api/v1/orientations/{id}")
    suspend fun deleteOrientation(@Path("id") id: String)
}
