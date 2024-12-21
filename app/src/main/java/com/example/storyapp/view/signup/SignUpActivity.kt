package com.example.storyapp.view.signup

import SignUpViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.SignUpViewModelFactory
import com.example.storyapp.view.login.LoginActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sembunyikan action bar
        supportActionBar?.hide()

        // Inisialisasi ViewModel
        val repository = Injection.provideSignUpRepository(this)
        viewModel = ViewModelProvider(this, SignUpViewModelFactory(repository))[SignUpViewModel::class.java]

        // Set tombol awal sebagai disabled
        binding.btnSignUp.setButtonState(false)

        // Hubungkan listener validasi dari CustomView
        binding.nameEditText.setValidationListener { updateRegisterButtonState() }
        binding.emailEditText.setValidationListener { updateRegisterButtonState() }
        binding.passwordEditText.setValidationListener { updateRegisterButtonState() }

        // Klik tombol register
        binding.btnSignUp.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            Log.d("SignUpActivity", "Inputs - Name: $name, Email: $email, Password: $password")
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Log.d("SignUpActivity", "Form suddenly invalid after click")
                Toast.makeText(this, "Semua field harus diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSignUp.setLoading(true)
            viewModel.register(name, email, password)
        }


        // Klik login
        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        observeViewModel()
    }

    private fun updateRegisterButtonState() {
        val nameValid = binding.nameEditText.isInputValid()
        val emailValid = binding.emailEditText.isInputValid()
        val passwordValid = binding.passwordEditText.isInputValid()

        val isFormValid = nameValid && emailValid && passwordValid

        Log.d("SignUpActivity", "Name valid: $nameValid, Email valid: $emailValid, Password valid: $passwordValid")
        Log.d("SignUpActivity", "Form valid after update: $isFormValid")

        binding.btnSignUp.setButtonState(isFormValid)
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            binding.btnSignUp.setLoading(false)

            result.onSuccess {
                Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            result.onFailure { throwable ->
                val errorMessage = throwable.message ?: "Terjadi kesalahan. Silakan coba lagi."
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                binding.btnSignUp.setButtonState(true)
            }
        }
    }
}



