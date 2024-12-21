package com.example.storyapp.data.repository

import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.retrofit.ApiService

class LoginRepository(private val apiService: ApiService) {
    suspend fun loginUser(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        if (response.error == true) {
            throw Exception(response.message)
        }
        return response
    }
}