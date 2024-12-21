package com.example.storyapp.view.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.local.response.UserPreferences
import com.example.storyapp.data.remote.response.ErrorResponse
import com.example.storyapp.data.repository.LoginRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val repository: LoginRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<String>>()
    var loginResult: MutableLiveData<Result<String>> = _loginResult

    fun login(email: String, password: String) {
        // Lakukan operasi API di background thread (IO)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // ambil hasil login dari repository
                val result = repository.loginUser(email, password)
                if (result.error == false) {
                    // simpan token ke shared preferences
                    val token = result.loginResult?.token
                    if (token != null) {
                        userPreferences.saveToken(token) // simpan token
                        _loginResult.postValue(Result.success(token)) // kirim token
                    }
                } else {
                    val errorMessage = result.message ?: "Login gagal"
                    _loginResult.postValue(Result.failure(Exception(errorMessage)))
                }
            } catch (e: HttpException) {
                // Tangkap error dari response body
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message ?: "Kesalahan server"

                // Posting pesan error
                _loginResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                // Tangani error lainnya
                val errorMessage = e.message ?: "Kesalahan tak terduga"
                _loginResult.postValue(Result.failure(Exception(errorMessage)))
            }
        }
    }
}
