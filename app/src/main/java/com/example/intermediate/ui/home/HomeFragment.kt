package com.example.intermediate.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.intermediate.R
import com.example.intermediate.data.api.ApiClientWithToken
import com.example.intermediate.data.datastore.DataStoreInstance
import com.example.intermediate.data.datastore.UserPreference
import com.example.intermediate.data.repository.StoryRepository
import com.example.intermediate.databinding.FragmentHomeBinding
import com.example.intermediate.ui.activity.DetailActivity
import com.example.intermediate.ui.activity.UploadActivity
import com.example.intermediate.ui.viewmodel.ViewModelFactory
import com.example.intermediate.util.UtilHelper
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var token: String
    private var isAllFabVisible: Boolean? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            UtilHelper.imageUri = uri
            startActivity(Intent(requireContext(), UploadActivity::class.java))
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            startActivity(Intent(requireContext(), UploadActivity::class.java))
        } else {
            UtilHelper.imageUri = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupDataStoreObserver()
        setupButton()
    }

    private fun setupButton() {
        binding.fabActionCamera.visibility = View.GONE
        binding.fabActionPicture.visibility = View.GONE

        isAllFabVisible = false
        binding.fabActionAdd.setOnClickListener{
            if (!isAllFabVisible!!) {
                binding.fabActionCamera.show()
                binding.fabActionPicture.show()
            } else {
                binding.fabActionCamera.hide()
                binding.fabActionPicture.hide()
            }
            isAllFabVisible = !isAllFabVisible!!
        }

        binding.fabActionCamera.setOnClickListener {
            startCamera()
        }

        binding.fabActionPicture.setOnClickListener {
            startGallery()
        }
    }

    private fun setupAdapter() {
        homeAdapter = HomeAdapter(object : HomeAdapter.OnItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(storyId: String) {
                startActivity(Intent(requireContext(), DetailActivity::class.java))
                UtilHelper.storyId = storyId
            }
        })
        layoutManager = LinearLayoutManager(requireContext())
        binding.rvStoryList.layoutManager = layoutManager
        binding.rvStoryList.adapter = homeAdapter
    }

    private fun setupDataStoreObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@HomeFragment.token = token
                if (this@HomeFragment::token.isInitialized) {
                    setupViewModel(token)
                    getStories()
                    setupViewModelObservers()
                }
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = ViewModelFactory(
            StoryRepository(ApiClientWithToken.create(token))
        )
        homeViewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
    }

    private fun getStories() {
        homeViewModel.getStories()
    }

    private fun setupViewModelObservers() {
        homeViewModel.stories.observe(viewLifecycleOwner) { stories ->
            homeAdapter.submitList(stories)
        }

        homeViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show()
                homeViewModel.resetExceptionValue()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        UtilHelper.imageUri = UtilHelper.getImageUri(requireActivity())
        launcherCamera.launch(UtilHelper.imageUri)
    }
}