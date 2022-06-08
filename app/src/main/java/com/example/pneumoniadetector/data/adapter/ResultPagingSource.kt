package com.example.pneumoniadetector.data.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pneumoniadetector.data.remote.response.ResultItem
import com.example.pneumoniadetector.data.remote.retrofit.ApiService

// if limitPage is more than 0 then the page is limited to how many limitPage inputed
class ResultPagingSource (private val apiService: ApiService, var limitPage: Int? = 0)
    : PagingSource<Int, ResultItem>() {

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ResultItem>): Int? {
        return state.anchorPosition?.let{ anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getResultList(page, params.loadSize).listStory

            LoadResult.Page(
                data = responseData as List<ResultItem>,
                prevKey = if(page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if(responseData.isNullOrEmpty() || page == limitPage) null else page + 1
            )
        }
        catch (exception: Exception){
            return LoadResult.Error(exception)
        }
    }


}