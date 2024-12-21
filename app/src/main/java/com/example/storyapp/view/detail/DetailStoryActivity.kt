package com.example.storyapp.view.detail

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.StoryViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var viewModel: DetailStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.sharedElementEnterTransition = TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move)
        window.sharedElementReturnTransition = TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move)


        // hide action bar
        supportActionBar?.hide()

        val id = intent.getStringExtra("EXTRA_STORY_ID") ?: ""

        // setup view model
        val factory = StoryViewModelFactory(Injection.provideStoryRepository(this))
        viewModel = ViewModelProvider(this, factory)[DetailStoryViewModel::class.java]

        // observe live data
        observeViewModel()

        // fetch detail story
        viewModel.fetchStoryDetail(id)
    }

    private fun observeViewModel() {
        viewModel.detailStory.observe(this) { story ->
            binding.apply {
                tvName.text = story.name
                tvDescription.text = story.description
                Glide.with(this@DetailStoryActivity)
                    .load(story.photoUrl)
                    .into(ivPhoto)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}