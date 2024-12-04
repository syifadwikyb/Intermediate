package com.example.intermediate.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.intermediate.MainActivity
import com.example.intermediate.R
import com.example.intermediate.data.api.ApiClientWithToken
import com.example.intermediate.data.datastore.DataStoreInstance
import com.example.intermediate.data.datastore.UserPreference
import com.example.intermediate.data.repository.StoryRepository
import com.example.intermediate.databinding.ActivityUploadBinding
import com.example.intermediate.ui.viewmodel.ViewModelFactory
import com.example.intermediate.util.UtilHelper
import kotlinx.coroutines.launch
import java.io.File

class UploadActivity : AppCompatActivity(R.layout.activity_upload) {
    private val binding by viewBinding(ActivityUploadBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Glide.with(this)
            .load(UtilHelper.imageUri)
            .fitCenter()
            .into(binding.ivUploadPhoto)

        setupButton()
    }

    private fun setupButton() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.buttonAdd.setOnClickListener {
            val photo = UtilHelper.uriToFile(UtilHelper.imageUri!!, this)
            setupUploadImage(photo, binding.edAddDescription.text.toString())
        }
    }

    private fun setupUploadImage(photo: File, description: String) {
        lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@UploadActivity.token = token
                if (this@UploadActivity::token.isInitialized) {
                    setupViewModel(token)
                    uploadImage(photo, description)
                    setupViewModelObservers()
                }
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = ViewModelFactory(StoryRepository(ApiClientWithToken.create(token)))
        uploadViewModel = ViewModelProvider(this, factory)[UploadViewModel::class.java]
    }

    private fun uploadImage(photo: File, description: String) {
        uploadViewModel.uploadStory(photo, description)
    }

    private fun setupViewModelObservers() {
        uploadViewModel.response.observe(this) { response ->
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

        uploadViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        uploadViewModel.exception.observe(this) { exception ->
            if (exception) {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
                uploadViewModel.resetExceptionValue()
            }
        }
    }
}