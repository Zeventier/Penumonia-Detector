package com.bangkit.pneumoniadetector.ui.preview

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.data.remote.response.PostPredictResponse
import com.bangkit.pneumoniadetector.data.remote.retrofit.ApiConfig
import com.bangkit.pneumoniadetector.databinding.ActivityPreviewBinding
import com.bangkit.pneumoniadetector.tools.FilePhotoTools
import com.bangkit.pneumoniadetector.ui.detail.EditDetailActivity
import com.bangkit.pneumoniadetector.ui.profile.EditProfileActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding
    private var getFile: File? = null

    // Create a storage reference from our app
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // hide action bar
        supportActionBar?.hide()

        // the button will go to MainActivity which will not allow user to go back to previous
        // activity such as CameraActivity or PreviewActivity(this activity)
        binding.btnFindResult.setOnClickListener {
            postPrediction()
        }

        // the button will finish or end the activity
        binding.ibBackButton.setOnClickListener { finish() }

        // will put photo from CameraActivity to imageView
        // EXTRA_IS_PICTURE is a safety to know that a file was sent to here
        if(intent.getBooleanExtra(EXTRA_IS_PICTURE, false)){

            getFile = intent.getSerializableExtra(EXTRA_PICTURE) as File
            getFile = FilePhotoTools.reduceFileImage(getFile as File, 1000000)

            val result = BitmapFactory.decodeFile((getFile as File).path)

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

    private fun postPrediction() {
        showLoading(true)
        val myFile = intent.getSerializableExtra(EditProfileActivity.EXTRA_PICTURE) as File

        val result = BitmapFactory.decodeFile(myFile.path)

        val requestImageFile = myFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            myFile.name,
            requestImageFile
        )

        val service =
            ApiConfig.getApiService()
                .postPredict(imageMultipart)
        service.enqueue(object : Callback<PostPredictResponse> {
            override fun onResponse(
                call: Call<PostPredictResponse>,
                response: Response<PostPredictResponse>
            ) {

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {

                        /*IMPORTANT!!
                        IF THE IMAGE IS ROTATED, USES THIS
                        val result = rotateBitmap(
                            BitmapFactory.decodeFile(myFile.path),
                            isBackCamera
                        )
                        */
                        val uniqueString = UUID.randomUUID().toString()
                        val path = "history/$uniqueString.jpg"
                        val fileRef = storageRef.child(path)

                        val baos = ByteArrayOutputStream()
                        result.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()

                        val uploadTask = fileRef.putBytes(data)
                        uploadTask.addOnFailureListener {
                            // Handle unsuccessful uploads
                            showLoading(false)
                            Toast.makeText(
                                this@PreviewActivity,
                                "Image failed to transfer to model",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnSuccessListener {
                            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                            Toast.makeText(
                                this@PreviewActivity,
                                "Image successfully transfered to model",
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
                            }.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showLoading(false)
                                    val user = Firebase.auth.currentUser
                                    val downloadUri = task.result
                                    val history = History(
                                        "Anon",
                                        responseBody.pneumoniaType,
                                        responseBody.probability,
                                        "",
                                        Calendar.getInstance().toString(),
                                        downloadUri.toString(),
                                        user?.uid
                                    )
                                    val intent = Intent(this@PreviewActivity, EditDetailActivity::class.java)
                                    intent.putExtra(EditDetailActivity.EXTRA_CALLED_FROM, FROM_PREVIEW_ACTIVITY)
                                    intent.putExtra(EditDetailActivity.EXTRA_DATA_BEFORE_EDIT, history)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(
                        this@PreviewActivity,
                        "onFailed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onFailed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostPredictResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@PreviewActivity,
                    "onFailure: ${t.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        const val EXTRA_PICTURE = "extra_picture"
        const val EXTRA_IS_PICTURE = "extra_is_picture"
        const val FROM_PREVIEW_ACTIVITY = 72
        private const val TAG = "PreviewActivity"
    }
}