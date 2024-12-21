package com.example.storyapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.data.local.response.UserPreferences
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.view.login.LoginActivity
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferences = UserPreferences.getInstance(this)

        GlobalScope.launch {
            val hasToken = userPreferences.hasToken()
            withContext(Dispatchers.Main) {
                if (hasToken) {
                    // Token ada, langsung ke Home
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Token tidak ada, arahkan ke Login
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
                finish() // Hancurkan SplashActivity
            }
        }

    }
}