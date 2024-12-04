package com.example.intermediate.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intermediate.data.model.Story
import com.example.intermediate.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> = _exception

    fun getStories() {
        if (_stories.value == null) {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _stories.postValue(storyRepository.getStories().listStory)
                    _exception.postValue(false)
                } catch (e: Exception) {
                    _exception.postValue(true)
                }
            }
        }
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}