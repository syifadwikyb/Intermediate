package com.example.intermediate.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.intermediate.R
import com.example.intermediate.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val settingsViewModel by lazy {
        ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.getUserNameAndEmail()
        settingsViewModel.username.observe(viewLifecycleOwner) {
            binding.tvProfileName.text = it
        }
        settingsViewModel.userEmail.observe(viewLifecycleOwner) {
            binding.tvProfileEmail.text = it
        }

        binding.llLanguageSetting.setOnClickListener {
            requireContext().startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.actionLogout.setOnClickListener {
            val logoutDialog = LogoutDialog()
            logoutDialog.show(parentFragmentManager, "LOGOUT_DIALOG")
        }
    }
}