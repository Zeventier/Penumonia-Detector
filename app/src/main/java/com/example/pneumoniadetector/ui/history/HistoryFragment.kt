package com.example.pneumoniadetector.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pneumoniadetector.data.adapter.LoadingStateAdapter
import com.example.pneumoniadetector.data.adapter.ResultListAdapter
import com.example.pneumoniadetector.data.remote.response.ResultItem
import com.example.pneumoniadetector.databinding.FragmentHistoryBinding
import com.example.pneumoniadetector.ui.detail.DetailActivity

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        historyViewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHistory()

        Glide.with(requireContext())
            .load("https://media.suara.com/pictures/653x366/2020/12/08/91579-david-gadgetin.jpg")
            .into(binding.imageViewPhoto)
    }

    // method for set up history recycler view with list of pneumonia results
    private fun setupHistory() {
        binding?.rvHistory?.layoutManager = LinearLayoutManager(context)
        val adapter = ResultListAdapter()
        binding.rvHistory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        historyViewModel.data.observe(viewLifecycleOwner){
            adapter.submitData(lifecycle, it)
        }
        // Dummy data
        adapter.submitData(lifecycle, historyViewModel.pagingTemp)

        adapter.setOnClickCallback(object: ResultListAdapter.OnItemClickCallback{
            override var data: ResultItem? = null
            override fun onItemClicked() {
                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}