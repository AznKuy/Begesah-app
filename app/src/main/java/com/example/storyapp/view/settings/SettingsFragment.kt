package com.example.storyapp.view.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.data.local.response.UserPreferences
import com.example.storyapp.databinding.FragmentSettingsBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.view.login.LoginActivity
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        userPreferences = Injection.provideUserPreferences(requireContext())

        // Logika tambahan, seperti Logout
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    // Proses logout
                    lifecycleScope.launch {
                        userPreferences.clearToken()
                        val token = userPreferences.getToken()
                        if (token.isNullOrEmpty()) {
                            Log.d("DEBUG_LOGOUT", "Token berhasil dihapus")
                            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Log.e("DEBUG_LOGOUT", "Token masih ada: $token")
                            Toast.makeText(requireContext(), "Logout gagal, coba lagi.", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                .setNegativeButton("Batal") { dialog, _ ->
                    // Tutup dialog
                    dialog.dismiss()
                }
                .show()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
