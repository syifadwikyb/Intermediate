package com.example.intermediate.data.repository

import com.example.intermediate.data.api.ApiService
import com.example.intermediate.data.model.LoginBody
import com.example.intermediate.data.model.LoginResponse
import com.example.intermediate.data.model.RegisterBody
import com.example.intermediate.data.model.RegisterResponse
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    suspend fun userRegister(registerBody: RegisterBody): Response<RegisterResponse> {
        return apiService.userRegister(registerBody)
    }

    suspend fun userLogin(loginBody: LoginBody): Response<LoginResponse> {
        return apiService.userLogin(loginBody)
    }
}