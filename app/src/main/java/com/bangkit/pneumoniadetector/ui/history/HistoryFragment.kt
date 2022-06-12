package com.bangkit.pneumoniadetector.ui.history

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.databinding.FragmentHistoryBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var database: DatabaseReference
    private var adapter = HistoryAdapter()
    private var isLoading = false
    private var isLastPage = false
    private var key: String? = null;

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

        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        binding.rvHistory.adapter = adapter
        binding.rvHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = LinearLayoutManager(context).childCount
                val totalItemCount = LinearLayoutManager(context).itemCount
                val firstVisibleItemPosition = LinearLayoutManager(context).findFirstVisibleItemPosition()

                if(!isLoading && !isLastPage)
                {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isLoading=true;
                        loadData();
                    }
                }
            }
        })


        loadData()

        setupHistory()

//        Glide.with(requireContext())
//            .load("https://media.suara.com/pictures/653x366/2020/12/08/91579-david-gadgetin.jpg")
//            .into(binding.imageViewPhoto)
    }

    // method for set up history recycler view with list of pneumonia results
    private fun setupHistory() {
//        binding.rvHistory.layoutManager = LinearLayoutManager(context)
//        val adapter = HistoryAdapter()



//        binding.rvHistory.adapter = adapter.withLoadStateFooter(
//            footer = LoadingStateAdapter{
//                adapter.retry()
//            }
//        )
//        historyViewModel.data.observe(viewLifecycleOwner){
//            adapter.submitData(lifecycle, it)
//        }
        // Dummy data
        //adapter.submitData(lifecycle, historyViewModel.pagingTemp)

//        adapter.setOnClickCallback(object: ResultListAdapter.OnItemClickCallback{
//            override var data: History? = null
//            override fun onItemClicked() {
//                val intent = Intent(requireActivity(), DetailActivity::class.java)
//                intent.putExtra(DetailActivity.EXTRA_DATA, data)
//                startActivity(intent)
//            }
//        })
    }

    private fun loadData()
    {
        val myUserId = Firebase.auth.currentUser?.uid
        get(key).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val historyList: MutableList<History> = mutableListOf()
                for (historySnapshot in dataSnapshot.children) {
                    val history: History? = historySnapshot.getValue(History::class.java)
                    if (history != null) {
                        if(history.userId == myUserId)
                            historyList.add(history)
                    } else {
                        isLastPage = true
                        break
                    }
                    key = historySnapshot.key
                }

                adapter.addItem(historyList)
                binding.rvHistory.adapter = adapter


                isLoading = false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }

    private fun get(key: String?) : Query
    {
        database = Firebase.database.reference

        if(key == null){
            return database.child("history").orderByKey().limitToFirst(5)
        }

        return database.child("history").orderByKey().startAfter(key).limitToFirst(5)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HistoryFragment"
    }
}