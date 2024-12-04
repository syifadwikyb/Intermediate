package com.example.intermediate.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.intermediate.MainActivity
import com.example.intermediate.R
import com.example.intermediate.data.datastore.DataStoreInstance
import com.example.intermediate.data.datastore.UserPreference
import com.example.intermediate.data.model.LoginBody
import com.example.intermediate.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val loginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRegisterLabel.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLoginLabel.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isEmpty()|| password.isEmpty()) {
                Toast.makeText(this, "Email and password can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.userLogin(LoginBody(email, password))
                this.email = email
            }
        }

        setupObserver()
    }

    private fun setupObserver() {
        loginViewModel.loginResponse.observe(this) { response ->
            lifecycleScope.launch {
                userPreference.updateUserLoginStatusAndToken(true, response.loginResult.token)
                userPreference.updateUsernameAndEmail(response.loginResult.name, email)
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        loginViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.exception.observe(this) { exception ->
            if (exception) {
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
                loginViewModel.resetExceptionValue()
            }
        }
    }
}