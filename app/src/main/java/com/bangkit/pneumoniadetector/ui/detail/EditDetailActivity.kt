package com.bangkit.pneumoniadetector.ui.detail

import android.content.Intent
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.pneumoniadetector.R
import com.example.pneumoniadetector.data.remote.response.ResultItem
import com.example.pneumoniadetector.databinding.ActivityEditDetailBinding
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

    private lateinit var binding: ActivityEditDetailBinding
    private lateinit var data: ResultItem
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red_1)))
        data = intent.getParcelableExtra<ResultItem>(EXTRA_DATA_BEFORE_EDIT) as ResultItem
        setupData(data)

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
    }

    private fun setupData(data: ResultItem) {
        binding.editTextName.setText(data.name)
        binding.textViewAccuracyContent.text = data.accuracy
        binding.textViewPredictionContent.text = data.pneumoniaType
        binding.textViewDescription.text = data.description
        Glide.with(applicationContext)
            .load(data.photoUrl)
            .into(binding.imageViewImage)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_detail_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item_save -> {

                // To know which activity call EditDetailActivity
                val calledFrom = intent.getIntExtra(EXTRA_CALLED_FROM, 0)
                when(calledFrom){
                    DetailActivity.FROM_DETAIL_ACTIVITY -> {
                        val intent = Intent()
                        data = saveData(data)
                        intent.putExtra(EXTRA_EDIT_DATA, data)
                        setResult(EDIT_DETAIL_RESULT, intent)
                        finish()
                        true
                    }
                    else -> {
                        Toast.makeText(
                            this@EditDetailActivity,
                            "No activity called this",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        false
                    }
                }
            }
            else -> false
        }
    }

    private fun saveData(data: ResultItem): ResultItem {
        data.name = binding.editTextName.text.toString()
        return data
    }

    companion object{
        const val EXTRA_EDIT_DATA = "extra_edit_data"
        const val EDIT_DETAIL_RESULT = 10
        const val EXTRA_CALLED_FROM = "extra_called_from"
        const val EXTRA_DATA_BEFORE_EDIT = "extra_data_before_edit"
    }
}