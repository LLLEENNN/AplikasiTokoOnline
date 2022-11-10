package com.anjati.elektronik.paging

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.anjati.elektronik.model.Produk
import com.anjati.elektronik.network.RetroService

class ProductPagingSource(val apiService: RetroService): PagingSource<Int, Produk>()  {

    override fun getRefreshKey(state: PagingState<Int, Produk>): Int? {

        return state.anchorPosition

    }

    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Produk> {
        return try {
            val nextPage: Int = params.key ?: FIRST_PAGE_INDEX
            val response = apiService.getDataFromAPI(nextPage)

            var nextPageNumber: Int? = null
            if(response?.next_page_url != null) {
                val uri = Uri.parse(response?.next_page_url!!)
                val nextPageQuery = uri.getQueryParameter("page")
                nextPageNumber = nextPageQuery?.toInt()
            }
            var prevPageNumber: Int? = null
            if(response?.prev_page_url != null) {
                val uri = Uri.parse(response?.prev_page_url!!)
                val prevPageQuery = uri.getQueryParameter("page")

                prevPageNumber = prevPageQuery?.toInt()
            }

            PagingSource.LoadResult.Page(data = response.data,
                    prevKey = prevPageNumber,
                    nextKey = nextPageNumber)
        }
        catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }
    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }

}