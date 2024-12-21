package com.example.storyapp.view.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityCreateStoryBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.ui.StoryViewModelFactory
import com.example.storyapp.utils.Result
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Suppress("DEPRECATION")
class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding

    private var currentImageUri: Uri? = null
    private lateinit var uploadViewModel: UploadViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Set up ViewModel
        val factory = StoryViewModelFactory(Injection.provideStoryRepository(this))
        uploadViewModel = ViewModelProvider(this, factory)[UploadViewModel::class.java]

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }

        binding.uploadButton.setValidationCallback {
            // Validasi apakah deskripsi tidak kosong dan gambar sudah dipilih
            val description = binding.descriptionEditText.text.toString()
            if (description.isEmpty()) {
                showToast(getString(R.string.empty_description_warning))
                binding.descriptionEditText.error = getString(R.string.empty_description_warning)
                return@setValidationCallback false
            }
            if (currentImageUri == null) {
                showToast(getString(R.string.empty_image_warning))
                return@setValidationCallback false
            }
            true // Validasi berhasil
        }
//        setupValidation()

        // Pulihkan URI gambar jika tersedia
        currentImageUri = savedInstanceState?.getParcelable("currentImageUri")
        if (currentImageUri != null) {
            showImage() // Tampilkan gambar yang telah dipilih
        }

    }

    private fun startGallery() {
        laucherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val laucherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descriptionEditText.text.toString()
            if (description.isEmpty()) {
                showToast(getString(R.string.empty_description_warning))
            }

            binding.uploadButton.setLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            uploadViewModel.uploadStory(multipartBody, requestBody, null, null).observe(this) { result ->
                when (result) {
                    is Result.Loading -> binding.uploadButton.setLoading(true) // Tampilkan loading
                    is Result.Success -> {
                        showToast(result.data.message ?: "Berhasil mengunggah cerita")
                        binding.uploadButton.setLoading(false)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    is Result.Failure -> {
                        showToast(result.error.localizedMessage ?: "Gagal mengunggah cerita")
                        Log.e("Upload Error", result.error.localizedMessage ?: "Gagal mengunggah cerita")
                        binding.uploadButton.setLoading(false)
                    }
                }
            }

        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    private fun setupValidation() {
//        // Listener untuk validasi deskripsi
//        binding.descriptionEditText.validationType = MyEditText.ValidationType.DESCRIPTION
//
//        binding.uploadButton.setValidationCallback {
//            val isDescriptionValid = binding.descriptionEditText.validateInput()
//            val isImageSelected = currentImageUri != null
//
//            if (!isImageSelected) {
//                Toast.makeText(this, getString(R.string.empty_image_warning), Toast.LENGTH_SHORT).show()
//            }
//
//            isDescriptionValid && isImageSelected
//        }
//    }

    // menyimpan gambar ketika layar dirotasi
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("currentImageUri", currentImageUri)
    }

}
