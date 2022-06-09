package com.example.pneumoniadetector.ui.camera


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.pneumoniadetector.R
import com.example.pneumoniadetector.databinding.ActivityCameraBinding
import com.example.pneumoniadetector.tools.FilePhotoTools
import com.example.pneumoniadetector.ui.preview.PreviewActivity

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // set btn_back and btn_flash icon color to black
        binding.btnBack.apply {
            setIconTintResource(R.color.white)
            setOnClickListener { finish() }
        }
        binding.btnFlash.setIconTintResource(R.color.white)


        binding.btnTakePhoto.setOnClickListener { takePhoto() }

    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    // method for preparing camera
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also{
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }
            catch(exc: Exception){
                Toast.makeText(this@CameraActivity, getString(R.string.failed_to_show_camera), Toast.LENGTH_SHORT)
                    .show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // method for clicking btn_take_photo that will take a photo
    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val photoFile = FilePhotoTools.createFile(application)

        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity,"Gagal mengambil gambar.", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent(this@CameraActivity, PreviewActivity::class.java).apply {
                        putExtra(PreviewActivity.EXTRA_PICTURE, photoFile)
                        putExtra(PreviewActivity.EXTRA_IS_PICTURE, true)
                    }
                    startActivity(intent)
                }
            }
        )

    }
}