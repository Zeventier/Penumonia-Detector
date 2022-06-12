package com.bangkit.pneumoniadetector.data.adapter

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.data.remote.retrofit.ApiService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


// if limitPage is more than 0 then the page is limited to how many limitPage inputed
class ResultPagingSource (private val apiService: ApiService, private var limitPage: Int? = 0)
    : PagingSource<Int, History>() {

    private lateinit var database: DatabaseReference
    private var key: String? = null

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
        private const val TAG = "ResultPagingSource"
    }

    private fun get(key: String?, size: Int) : Query
    {
        database = Firebase.database.reference

        if(key == null){
            return database.child("history").orderByKey().limitToFirst(size)
        }

        return database.child("history").orderByKey().startAfter(key).limitToFirst(size)
    }

    override fun getRefreshKey(state: PagingState<Int, History>): Int? {
        return state.anchorPosition?.let{ anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, History> {
        try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            //val responseData = apiService.getResultList(page, params.loadSize).listStory
            val historyList: List<History> = emptyList()

            get(key, params.loadSize).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (historySnapshot in dataSnapshot.children) {
                        val history: History? = historySnapshot.getValue(History::class.java)
                        historyList.plus(history)
                        key = historySnapshot.key
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                    // ...
                }
            })


            return LoadResult.Page(
                data =  historyList,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (historyList.isEmpty() || page == limitPage) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }


}