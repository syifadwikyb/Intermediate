package com.example.intermediate.data.model

data class LoginBody(
    val email: String,
    val password: String
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult,
)

data class RegisterBody(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val error: Boolean,
    val message: String
)

data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Float?,
    val lon: Float?
)

data class HeaderStories(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)

data class HeaderStory(
    val error: Boolean,
    val message: String,
    val story: Story
)
