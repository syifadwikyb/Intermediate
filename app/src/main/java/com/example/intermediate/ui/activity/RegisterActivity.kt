package com.example.intermediate.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.intermediate.R
import com.example.intermediate.data.model.RegisterBody
import com.example.intermediate.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity(R.layout.activity_register) {
    private val binding by viewBinding(ActivityRegisterBinding::bind)
    private val registerViewModel by lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBackButton()
        setupRegisterButton()
        setupObservers()

    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val username = binding.edRegisterName.text.toString()
            val userEmail = binding.edRegisterEmail.text.toString()
            val userPassword = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edRegisterConfirmPassword.text.toString()

            if (listOf(username, userEmail, userPassword, confirmPassword).any { it.isEmpty() }) {
                Toast.makeText(this, "Data can't be empty", Toast.LENGTH_SHORT).show()
            } else if (userPassword != confirmPassword) {
                Toast.makeText(this, "Password and Confirm Password not match!", Toast.LENGTH_SHORT).show()
            } else {
                registerViewModel.userRegister(RegisterBody(username, userEmail, userPassword))
            }
        }
    }

    private fun setupObservers() {
        registerViewModel.registerResponse.observe(this) { response ->
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            finish()
        }

        registerViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        registerViewModel.exception.observe(this) { exception ->
            if (exception) {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
                registerViewModel.resetExceptionValue()
            }
        }
    }
}