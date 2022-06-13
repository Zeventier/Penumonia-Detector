package com.bangkit.pneumoniadetector.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.databinding.ActivityEditProfileBinding
import com.bangkit.pneumoniadetector.tools.GeneralTools
import com.bangkit.pneumoniadetector.ui.MainActivity
import com.bangkit.pneumoniadetector.ui.camera.CameraProfileActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var getFile: File? = null
    // Create a storage reference from our app
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = null
        //actionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val user = Firebase.auth.currentUser

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

            binding.imageViewPhoto.setImageBitmap(result)
        } else if(user?.photoUrl != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .into(binding.imageViewPhoto)
        } else {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    binding.imageViewPhoto.setImageResource(R.drawable.photo_profile_default)
                } // Night mode is not active, we're using the light theme
                Configuration.UI_MODE_NIGHT_YES -> {
                    binding.imageViewPhoto.setImageResource(R.drawable.photo_profile_default_white)
                } // Night mode is active, we're using dark theme
            }
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_CAMERA_PERMISSION,
                REQUEST_CAMERA_CODE_PERMISSION
            )
        }

        setupAction()
    }

    private fun setupAction() {
        val user = Firebase.auth.currentUser
        binding.etName.setText(user?.displayName.toString())
        binding.etEmail.setText(user?.email.toString())

        binding.btnSave.setOnClickListener {
            saveEdit()
        }

        binding.imageViewBtnPhoto.setOnClickListener {
            goToCamera()
        }
    }

    private fun saveEdit()
    {
        val user = Firebase.auth.currentUser
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
                showLoading(true)
                if(intent.getBooleanExtra(EXTRA_IS_PICTURE, false)) {
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
                    val uniqueString = UUID.randomUUID().toString()
                    val path = "profilePic/$uniqueString.jpg"
                    val fileRef = storageRef.child(path)

                    val baos = ByteArrayOutputStream()
                    result.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    val uploadTask = fileRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profile Picture failed to update",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnSuccessListener {
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profile Picture updated",
                            Toast.LENGTH_SHORT
                        ).show()
                        uploadTask.continueWithTask { task ->
                            if(!task.isSuccessful) {
                                showLoading(false)
                                task.exception?.let {
                                    throw it
                                }
                            }
                            fileRef.downloadUrl
                        }.addOnCompleteListener { taskOne ->
                            if (taskOne.isSuccessful) {
                                val downloadUri = taskOne.result
                                val profileUpdates = userProfileChangeRequest {
                                    displayName = name
                                    photoUri = downloadUri
                                    Log.d(TAG, photoUri.toString())
                                }
                                    user!!.updateProfile(profileUpdates)
                                        .addOnCompleteListener { taskTwo ->
                                            if (taskTwo.isSuccessful) {
                                                Log.d(TAG, "User profile updated.")
                                                user.updateEmail(email)
                                                    .addOnCompleteListener { taskThree ->
                                                        if (taskThree.isSuccessful) {
                                                            showLoading(false)
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
                } else {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { taskFour ->
                            if (taskFour.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                user.updateEmail(email)
                                    .addOnCompleteListener { taskFive ->
                                        if (taskFive.isSuccessful) {
                                            showLoading(false)
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

    private fun goToCamera()
    {
        // check camera permission
        if(!GeneralTools.allPermissionGranted(REQUIRED_CAMERA_PERMISSION, this)){
            ActivityCompat.requestPermissions(
                EditProfileActivity(),
                REQUIRED_CAMERA_PERMISSION,
                REQUEST_CAMERA_CODE_PERMISSION
            )
        }
        else{
            val intent = Intent(this, CameraProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_CAMERA_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CAMERA_CODE_PERMISSION){
            if(!GeneralTools.allPermissionGranted(REQUIRED_CAMERA_PERMISSION, this)){
                GeneralTools.showAlertDialog(this, getString(R.string.camera_usage_not_granted))
            }
            else{
                Toast.makeText(
                    this,
                    getString(R.string.camera_usage_granted),
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "EditProfileActivity"

        val REQUIRED_CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CAMERA_CODE_PERMISSION = 10

        const val EXTRA_PICTURE = "extra_picture"
        const val EXTRA_IS_PICTURE = "extra_is_picture"
    }
}