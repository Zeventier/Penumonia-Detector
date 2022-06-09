package com.bangkit.pneumoniadetector.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.databinding.ActivityEditProfileBinding
import com.bangkit.pneumoniadetector.databinding.ActivityMainBinding
import com.bangkit.pneumoniadetector.ui.MainActivity
import com.bangkit.pneumoniadetector.ui.login.LoginActivity
import com.bangkit.pneumoniadetector.ui.register.RegisterActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = null
        //actionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupAction()
    }

    private fun setupAction() {
        val user = Firebase.auth.currentUser
        binding.etName.setText(user?.displayName.toString())
        binding.etEmail.setText(user?.email.toString())

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            when {
                name.isEmpty() -> {
                    binding.etName.error = "Masukkan nama"
                }
                email.isEmpty() -> {
                    binding.etEmail.error = "Masukkan email"
                }
                else -> {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                user.updateEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "User email address updated.")
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                            }
                        }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "EditProfileActivity"
    }
}