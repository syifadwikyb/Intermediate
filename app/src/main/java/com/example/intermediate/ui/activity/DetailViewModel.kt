package com.example.intermediate.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intermediate.data.repository.StoryRepository
import com.example.intermediate.data.model.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _exception = MutableLiveData<Boolean>()
    val exception: LiveData<Boolean> = _exception

    fun getDetailStory(storyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _story.postValue(storyRepository.getDetailStory(storyId).story)
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