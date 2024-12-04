package com.example.intermediate.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intermediate.data.model.RegisterResponse
import com.example.intermediate.data.repository.StoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _response = MutableLiveData<RegisterResponse>()
    private val _errorMessage = MutableLiveData<String>()
    private val _exception = MutableLiveData<Boolean>()

    val response: LiveData<RegisterResponse> = _response
    val errorMessage: LiveData<String> = _errorMessage
    val exception: LiveData<Boolean> = _exception

    fun uploadStory(photo: File, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = storyRepository.uploadStory(photo, description)

                if (response.isSuccessful) {
                    _response.postValue(response.body())
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