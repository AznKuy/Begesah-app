package com.example.storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.local.response.UserPreferences
import com.example.storyapp.data.repository.LoginRepository
import com.example.storyapp.view.login.LoginViewModel

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val repository: LoginRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}