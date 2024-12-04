package com.example.intermediate.data.repository

import com.example.intermediate.data.api.ApiService
import com.example.intermediate.data.model.HeaderStories
import com.example.intermediate.data.model.HeaderStory
import com.example.intermediate.data.model.RegisterResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class StoryRepository(private val apiService: ApiService) {
    suspend fun getStories(): HeaderStories {
        return apiService.getStories(30)
    }

    suspend fun getDetailStory(storyId: String): HeaderStory {
        return apiService.getDetailStory(storyId)
    }

    suspend fun uploadStory(photo: File, description: String): Response<RegisterResponse> {
        val requestPhoto = photo.asRequestBody("image/jpeg".toMediaType())
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val multipartPhoto = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestPhoto
        )

        return apiService.uploadStory(multipartPhoto, requestDescription)
    }
}