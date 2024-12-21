package com.example.storyapp.data.remote.retrofit

import android.util.Log
import com.example.storyapp.data.local.response.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig(private val userPreferences: UserPreferences) {

    fun getApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { userPreferences.getToken() } // Ambil token dari DataStore
            if (!token.isNullOrEmpty()) {
                Log.d("DEBUG_INTERCEPTOR", "Token digunakan: Bearer $token")
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            } else {
                Log.e("DEBUG_INTERCEPTOR", "Token kosong atau tidak ditemukan.")
                chain.proceed(chain.request()) // Lanjutkan tanpa token jika kosong
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Logging untuk debug
            .addInterceptor(authInterceptor)    // Tambahkan header Authorization
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}


