package com.example.storyapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.data.local.response.UserPreferences
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.LoginViewModelFactory
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.view.signup.SignUpActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // hide action bar
        supportActionBar?.hide()

        // userpreferences
        userPreferences = Injection.provideUserPreferences(this)

        // Injection untuk mengambil data dari ViewModel
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(
                Injection.provideLoginRepository(this),
                userPreferences
            )
        )[LoginViewModel::class.java]

        // Set kondisi awal tombol login (disable)
        binding.btnLogin.setButtonState(false)

        // Valisasi email dan password custom view
        binding.emailEditText.setValidationListener { updateLoginButtonState() }
        binding.passwordEditText.setValidationListener { updateLoginButtonState() }

        // Listener untuk login
        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            binding.btnLogin.setLoading(true)

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.login(email, password)
        }

        // ke halaman register
        binding.registerTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Observasi hasil login
        setObserver()
    }

    private fun updateLoginButtonState() {
        val emailValid = binding.emailEditText.isInputValid()
        val passwordValid = binding.passwordEditText.isInputValid()
        binding.btnLogin.setButtonState(emailValid && passwordValid)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun setObserver() {
        viewModel.loginResult.observe(this) { result ->
            binding.btnLogin.setLoading(false) // Hentikan loading

            result.onSuccess { token ->
                lifecycleScope.launch {
                    // Ambil token dari respons login
                    userPreferences.saveToken(token)
                    Log.d("DEBUG_LOGIN", "Token baru disimpan: $token")
                    Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                    // Redirect ke MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            result.onFailure { throwable ->
                // Tampilkan pesan error jika login gagal
                val errorMessage = throwable.message ?: "Login gagal. Pastikan email dan password benar."
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

                // Aktifkan kembali tombol login
                binding.btnLogin.setButtonState(true)
            }
        }
    }


}
