package com.example.intermediate.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intermediate.data.api.ApiClient
import com.example.intermediate.data.model.LoginBody
import com.example.intermediate.data.model.LoginResponse
import com.example.intermediate.data.model.RegisterResponse
import com.example.intermediate.data.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){
    private val userRepository = UserRepository(ApiClient.apiClient)
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> = _exception

    fun userLogin(loginBody: LoginBody) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userRepository.userLogin(loginBody)

                if (response.isSuccessful) {
                    _loginResponse.postValue(response.body())
                } else {
                    val errorJson = response.errorBody()?.string()
                    val apiError = Gson().fromJson(errorJson, RegisterResponse::class.java)
                    _errorMessage.postValue(apiError.message)
                }
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}