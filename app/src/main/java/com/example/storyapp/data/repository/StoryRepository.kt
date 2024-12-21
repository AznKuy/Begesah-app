package com.example.storyapp.data.repository

import com.example.storyapp.data.local.response.DetailStoryResponse
import com.example.storyapp.data.local.response.StoryResponse
import com.example.storyapp.data.local.response.UploadStoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService
) {
    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }
    }

    // ambil semua story
    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }

    // detail story
    suspend fun getDetailStory(id: String): DetailStoryResponse {
        return apiService.getDetailStory(id)
    }

    // upload story
    suspend fun uploadStory(file: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): UploadStoryResponse {
        return apiService.uploadStory(file, description, lat, lon)
    }
}