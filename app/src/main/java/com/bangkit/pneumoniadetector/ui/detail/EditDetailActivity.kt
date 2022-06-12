package com.bangkit.pneumoniadetector.ui.detail

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.databinding.ActivityEditDetailBinding
import com.bangkit.pneumoniadetector.ui.preview.PreviewActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditDetailBinding
    private lateinit var data: History
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red_1)))
        data = intent.getParcelableExtra<History>(EXTRA_DATA_BEFORE_EDIT) as History
        setupData(data)

        db = Firebase.database

        //Testing Realtime Database
//        binding.apply {
//            textViewPredictionContent.text = "Pneumonia Covid"
//            textViewAccuracyContent.text = "99%"
//            textViewDescription.text = "Lorem Ipsum"
//        }

    }

    private fun setupData(data: History) {
        binding.editTextName.setText(data.name.toString())
        binding.textViewAccuracyContent.text = data.probability.toString()
        binding.textViewPredictionContent.text = data.prediction.toString()
        binding.textViewDescription.text = {
            when (data.prediction) {
                "viral" -> "Lorem"
                "covid" -> "Lorammmmmm"
                "" -> "Loremmmm"
                else -> {
                    "BRUH"
                }
            }
        }.toString()
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
                when(intent.getIntExtra(EXTRA_CALLED_FROM, 0)){
                    DetailActivity.FROM_DETAIL_ACTIVITY -> {
                        val intent = Intent()
                        //data = saveData(data)
                        intent.putExtra(EXTRA_EDIT_DATA, data)
                        setResult(EDIT_DETAIL_RESULT, intent)
                        finish()
                        true
                    }
                    PreviewActivity.FROM_PREVIEW_ACTIVITY -> {
                        val history = History(
                            binding.editTextName.text.toString(),
                            binding.textViewPrediction.text.toString(),
                            binding.textViewAccuracy.text.toString(),
                            binding.textViewDescription.text.toString(),
                            data.createdAt,
                            data.userId
                        )
                        val historyRef = db.reference.child("history")
                        historyRef.push().setValue(history) { error, _ ->
                            if (error != null) {
                                Toast.makeText(this, "Error" + error.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                            }
                        }
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

//    private fun saveData(data: History): History {
//        data.name = binding.editTextName.text.toString()
//        return data
//    }

    companion object{
        const val EXTRA_EDIT_DATA = "extra_edit_data"
        const val EDIT_DETAIL_RESULT = 10
        const val EXTRA_CALLED_FROM = "extra_called_from"
        const val EXTRA_DATA_BEFORE_EDIT = "extra_data_before_edit"
    }
}