package com.bangkit.pneumoniadetector.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.databinding.ActivityEditDetailBinding
import com.bangkit.pneumoniadetector.databinding.ActivityMainBinding
import com.bangkit.pneumoniadetector.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditDetailActivity : AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    private lateinit var binding: ActivityEditDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.database

        val historyRef = db.reference.child("history")

        //Testing Realtime Database
//        binding.apply {
//            textViewPredictionContent.text = "Pneumonia Covid"
//            textViewAccuracyContent.text = "99%"
//            textViewDescription.text = "Lorem Ipsum"
//        }

//        binding.btnSave.setOnClickListener {
//            val history = History(
//                "asdaw",
//                "asdwa",
//                "awdas",
//                "awdasd",
//                "",
//                Firebase.auth.currentUser?.uid.toString()
//            )
//
//            historyRef.push().setValue(history) { error, _ ->
//                if (error != null) {
//                    Toast.makeText(this, "Error" + error.message, Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        isLogin()
    }

    private fun isLogin() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}