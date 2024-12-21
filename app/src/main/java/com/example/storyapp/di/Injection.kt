@file:Suppress("unused", "unused")

package com.example.storyapp.di

import SignUpViewModel
import android.content.Context
import com.example.storyapp.data.local.response.UserPreferences
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.data.repository.LoginRepository
import com.example.storyapp.data.repository.SignUpRepository
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.view.login.LoginViewModel
import kotlinx.coroutines.runBlocking

@Suppress("unused")
object Injection {

    // UserPreferences Instance
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences.getInstance(context)
    }

    // ApiService Instance
    private fun provideApiService(context: Context): ApiService {
        val userPreferences = provideUserPreferences(context)
        val token = runBlocking { userPreferences.getToken() } ?: "" // Ambil token dari DataStore
        return ApiConfig(userPreferences).getApiService() // Buat ApiService dengan token
    }

    // StoryRepository Instance
    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = provideApiService(context)
        return StoryRepository.getInstance(apiService) // Instance StoryRepository
    }

    // LoginRepository Instance
    fun provideLoginRepository(context: Context): LoginRepository {
        val apiService = provideApiService(context)
        return LoginRepository(apiService)
    }

    // SignUpRepository Instance
    fun provideSignUpRepository(context: Context): SignUpRepository {
        val apiService = provideApiService(context)
        return SignUpRepository(apiService)
    }

    // LoginViewModel Instance
    fun provideLoginViewModel(context: Context): LoginViewModel {
        val repository = provideLoginRepository(context)
        val userPreferences = provideUserPreferences(context)
        return LoginViewModel(repository, userPreferences)
    }

    // SignUpViewModel Instance
    fun provideSignUpViewModel(context: Context): SignUpViewModel {
        val repository = provideSignUpRepository(context)
        return SignUpViewModel(repository)
    }
}
