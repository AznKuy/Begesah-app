package com.example.storyapp.view.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.StoryViewModelFactory
import com.example.storyapp.view.story.StoryAdapter
import com.example.storyapp.view.story.StoryViewModel
import com.example.storyapp.view.upload.CreateStoryActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val factory = StoryViewModelFactory(Injection.provideStoryRepository(requireContext()))
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(emptyList())
        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.adapter = storyAdapter
    }

    private fun observeViewModel() {
        storyViewModel.stories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.setStoryList(stories)
        }

        storyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        storyViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabCreateStory.setOnClickListener {
            val intent = Intent(requireContext(), CreateStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        storyViewModel.fetchStories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
