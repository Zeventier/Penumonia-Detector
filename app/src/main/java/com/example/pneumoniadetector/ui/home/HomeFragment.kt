package com.example.pneumoniadetector.ui.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pneumoniadetector.data.adapter.LoadingStateAdapter
import com.example.pneumoniadetector.data.adapter.ResultListAdapter
import com.example.pneumoniadetector.data.remote.response.ResultItem
import com.example.pneumoniadetector.databinding.FragmentHomeBinding
import com.example.pneumoniadetector.tools.GeneralTools
import com.example.pneumoniadetector.ui.camera.CameraActivity
import com.example.pneumoniadetector.ui.detail.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

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

        // when click cardView, goes to CameraActivity when having camera permission granted
        binding.materialCardView.setOnClickListener { goesToCameraActivity() }
        setupRecent()

        Glide.with(requireContext())
            .load("https://media.suara.com/pictures/653x366/2020/12/08/91579-david-gadgetin.jpg")
            .into(binding.imageViewPhoto)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method for clicking materialCardView that will goes to CameraActivity when permission granted
    fun goesToCameraActivity() {

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
        binding?.rvRecent?.layoutManager = LinearLayoutManager(context)
        val adapter = ResultListAdapter()
        binding.rvRecent.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        homeViewModel.data.observe(viewLifecycleOwner){
            adapter.submitData(lifecycle, it)
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
    }
}