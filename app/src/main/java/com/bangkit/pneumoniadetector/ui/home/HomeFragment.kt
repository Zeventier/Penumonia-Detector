package com.bangkit.pneumoniadetector.ui.home

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.databinding.FragmentHomeBinding
import com.bangkit.pneumoniadetector.tools.GeneralTools
import com.bangkit.pneumoniadetector.ui.camera.CameraActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.pneumoniadetector.data.adapter.LoadingStateAdapter
import com.bangkit.pneumoniadetector.data.adapter.ResultListAdapter
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.data.remote.response.ResultItem
import com.bangkit.pneumoniadetector.ui.detail.DetailActivity
import com.bangkit.pneumoniadetector.ui.history.HistoryAdapter
import com.bangkit.pneumoniadetector.ui.history.HistoryFragment
import com.bangkit.pneumoniadetector.ui.history.RecentAdapter
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var database: DatabaseReference
    private var isLastPage = false
    private var adapter = RecentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = Firebase.auth.currentUser

        if(user?.photoUrl != null) {
            Glide.with(FragmentActivity())
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

        binding.textViewName.text = "Hi, " + user?.displayName.toString()

        binding.rvRecent.layoutManager = LinearLayoutManager(context)
        binding.rvRecent.adapter = adapter
        if(!isLastPage)
            loadData()

        // when click cardView, goes to CameraActivity when having camera permission granted
        binding.imageViewButton.setOnClickListener { goesToCameraActivity() }

        //setupRecent()
    }

    private fun loadData() {
        val myUserId = Firebase.auth.currentUser?.uid
        get().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val historyList: MutableList<History> = mutableListOf()
                for (historySnapshot in dataSnapshot.children) {
                    val history: History? = historySnapshot.getValue(History::class.java)
                    if (history != null) {
                        if(history.userId == myUserId)
                            historyList.add(history)
                    }
                }

                adapter.addItem(historyList)
                binding.rvRecent.adapter = adapter
                isLastPage = true
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }

    private fun get() : Query
    {
        database = Firebase.database.reference

        return database.child("history").orderByKey().limitToFirst(3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method for clicking materialCardView that will goes to CameraActivity when permission granted
    private fun goesToCameraActivity() {

        // check camera permission
        if(!GeneralTools.allPermissionGranted(REQUIRED_CAMERA_PERMISSION, requireContext())){
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_CAMERA_PERMISSION,
                REQUEST_CAMERA_CODE_PERMISSION
            )
        }
        else{
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }
    }

    // method for set up history recycler view with list of pneumonia results
    private fun setupRecent() {
        binding.rvRecent.layoutManager = LinearLayoutManager(context)
        val adapter = ResultListAdapter()
        binding.rvRecent.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        homeViewModel.data.observe(viewLifecycleOwner){
            //adapter.submitData(lifecycle, it)
        }

        // Dummy data
        adapter.submitData(lifecycle, homeViewModel.pagingTemp)

        adapter.setOnClickCallback(object: ResultListAdapter.OnItemClickCallback{
            override var data: ResultItem? = null
            override fun onItemClicked() {
                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }

    companion object{
        val REQUIRED_CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CAMERA_CODE_PERMISSION = 10
        private const val TAG = "HistoryFragment"
    }
}