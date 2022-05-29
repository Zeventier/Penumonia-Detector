package com.example.pneumoniadetector.ui.landingpage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pneumoniadetector.R
import com.example.pneumoniadetector.databinding.ActivityLandingPageBinding
import com.example.pneumoniadetector.databinding.ActivityMainBinding
import com.example.pneumoniadetector.ui.login.LoginActivity
import com.example.pneumoniadetector.ui.register.RegisterActivity

class LandingPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.btnGetStarted.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvLogin2.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}