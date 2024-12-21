package com.example.storyapp.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.local.response.Story
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _detailStory = MutableLiveData<Story>()
    val detailStory: MutableLiveData<Story> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: MutableLiveData<String> = _errorMessage

    fun fetchStoryDetail(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDetailStory(id)
                if (response.error == false) {
                    _detailStory.postValue(response.story)
                } else {
                    _errorMessage.postValue(response.message)
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error fetching story detail")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}