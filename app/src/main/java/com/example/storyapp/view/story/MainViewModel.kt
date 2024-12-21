package com.example.storyapp.view.story

import androidx.lifecycle.liveData
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.utils.Result

class MainViewModel(private val repository: StoryRepository) {
    fun getStories() = liveData {
        emit(Result.loading()) // Emit status loading
        try {
            val stories = repository.getStories() // Ambil data cerita
            emit(Result.success(stories)) // Emit data jika berhasil
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit error jika gagal
        }
    }
}