package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.remote.dto.AuthResponseDto
import com.fiap.ariachallenge.data.remote.dto.IdeaDto
import com.fiap.ariachallenge.data.remote.dto.LoginRequestDto
import com.fiap.ariachallenge.data.remote.dto.OrientationDto
import com.fiap.ariachallenge.data.remote.dto.ProjectDto
import com.fiap.ariachallenge.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AriaApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @GET("api/v1/ideas")
    suspend fun getIdeas(): List<IdeaDto>

    @POST("api/v1/ideas")
    suspend fun createIdea(@Body idea: IdeaDto): IdeaDto

    @PUT("api/v1/ideas/{id}")
    suspend fun updateIdea(@Path("id") id: String, @Body idea: IdeaDto): IdeaDto

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
