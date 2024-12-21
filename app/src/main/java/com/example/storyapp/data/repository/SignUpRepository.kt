package com.example.storyapp.data.repository

import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.retrofit.ApiService

class SignUpRepository(private val apiService: ApiService) {
    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse{
        return apiService.register(name, email, password)
    }
}