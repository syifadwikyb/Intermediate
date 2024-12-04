package com.example.intermediate.data.api

import com.example.intermediate.data.model.HeaderStories
import com.example.intermediate.data.model.HeaderStory
import com.example.intermediate.data.model.LoginBody
import com.example.intermediate.data.model.LoginResponse
import com.example.intermediate.data.model.RegisterBody
import com.example.intermediate.data.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun userRegister(@Body registerBody: RegisterBody): Response<RegisterResponse>

    @POST("login")
    suspend fun userLogin(@Body loginBody: LoginBody): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(@Query("size") size: Int): HeaderStories

    @GET("stories/{id}")
    suspend fun getDetailStory(@Path("id") id: String): HeaderStory

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<RegisterResponse>
}