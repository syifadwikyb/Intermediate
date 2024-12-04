package com.example.intermediate.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.intermediate.R
import com.example.intermediate.data.api.ApiClientWithToken
import com.example.intermediate.data.datastore.DataStoreInstance
import com.example.intermediate.data.datastore.UserPreference
import com.example.intermediate.data.repository.StoryRepository
import com.example.intermediate.databinding.ActivityDetailBinding
import com.example.intermediate.ui.viewmodel.ViewModelFactory
import com.example.intermediate.util.UtilHelper
import com.example.intermediate.util.UtilHelper.convertDateFormat
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class DetailActivity : AppCompatActivity(R.layout.activity_detail) {
    private val binding by viewBinding(ActivityDetailBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBackButton()
        setupBackPressedDispatcher()
        setupDataStoreObserver()
    }

    private fun setupBackButton() {
        binding.btnToolbarBack.setOnClickListener {
            detailViewModel.resetExceptionValue()
            finish()
        }
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    detailViewModel.resetExceptionValue()
                    finish()
                }
            }
        )
    }

    private fun setupDataStoreObserver() {
        lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@DetailActivity.token = token
                if (this@DetailActivity::token.isInitialized) {
                    setupViewModel(token)
                    getDetailStory(UtilHelper.storyId)
                    setupViewModelObservers()
                }
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = ViewModelFactory(
            StoryRepository(ApiClientWithToken.create(token))
        )
        detailViewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]
    }

    private fun getDetailStory(storyId: String) {
        detailViewModel.getDetailStory(storyId)
    }

    private fun setupViewModelObservers() {
        detailViewModel.story.observe(this) { story ->
            binding.apply {
                tvDetailName.text = story.name
                tvDetailDescription.text = story.description
                tvDetailDate.text = convertDateFormat(story.createdAt.substring(0, 10))

                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .fitCenter()
                    .into(ivDetailPhoto)
            }
        }

        detailViewModel.exception.observe(this) { exception ->
            if (exception) {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
                binding.tvNoConnectionMessage.visibility = View.VISIBLE
            } else {
                binding.tvNoConnectionMessage.visibility = View.GONE
            }
        }
    }
}