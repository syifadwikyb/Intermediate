package com.example.intermediate.ui.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.intermediate.MainActivity
import com.example.intermediate.R
import com.example.intermediate.data.datastore.DataStoreInstance
import com.example.intermediate.data.datastore.UserPreference
import com.example.intermediate.databinding.ActivitySplashBinding
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    private val binding by viewBinding(ActivitySplashBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAnimator(binding.tvAppTitle, resources.getString(R.string.app_name))
        setupDataStoreObservers()
    }

    private fun setupAnimator(textView: TextView, fullText: String) {
        binding.ivLogo.alpha = 0f
        textView.visibility = View.GONE
        binding.ivLogo.animate()
            .alpha(1f)
            .setDuration(1000)
            .withEndAction {
                val animator = ValueAnimator.ofInt(0, fullText.length)
                animator.duration = 1000
                animator.addUpdateListener { animation ->
                    val currentIndex = animation.animatedValue as Int
                    textView.text = fullText.substring(0, currentIndex)
                    textView.visibility = View.VISIBLE
                }
                animator.start()
            }.start()
    }

    private fun setupDataStoreObservers() {
        lifecycleScope.launch {
            userPreference.isUserLogin.collect { isLogin ->
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isLogin) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    }
                    finish()
                }, 2000)
            }
        }
    }
}