package com.example.pneumoniadetector.ui.preview

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pneumoniadetector.databinding.ActivityPreviewBinding
import com.example.pneumoniadetector.ui.MainActivity
import java.io.File

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // hide action bar
        supportActionBar?.hide()

        // the button will go to MainActivity which will not allow user to go back to previous
        // activity such as CameraActivity or PreviewActivity(this activity)
        binding.btnFindResult.setOnClickListener {
            // this intent is not fixed. You can change it to another activity
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // the button will finish or end the activity
        binding.ibBackButton.setOnClickListener { finish() }

        // will put photo from CameraActivity to imageView
        // EXTRA_IS_PICTURE is a safety to know that a file was sent to here
        if(intent.getBooleanExtra(EXTRA_IS_PICTURE, false)){

            val myFile = intent.getSerializableExtra(EXTRA_PICTURE) as File

            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)

            /*IMPORTANT!!
            IF THE IMAGE IS ROTATED, USES THIS
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
             */

            binding.ivImagePreview.setImageBitmap(result)
        }

    }

    companion object{
        const val EXTRA_PICTURE = "extra_picture"
        const val EXTRA_IS_PICTURE = "extra_is_picture"
    }
}