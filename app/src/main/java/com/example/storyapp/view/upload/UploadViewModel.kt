package com.example.storyapp.view.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.storyapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import com.example.storyapp.utils.Result
import okhttp3.RequestBody

class UploadViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadStory(file: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?) =
        liveData {
            emit(Result.loading())
            try {
                val response = repository.uploadStory(file, description, lat, lon)
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))

            }
        }
}