package com.anjati.elektronik.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.anjati.elektronik.model.Produk
import com.anjati.elektronik.network.RetroInstance
import com.anjati.elektronik.network.RetroService
import com.anjati.elektronik.paging.ProductPagingSource
import kotlinx.coroutines.flow.Flow

class HomeViewModel: ViewModel() {

    lateinit var retroService: RetroService

    init {
        retroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
    }

    fun getListData(): Flow<PagingData<Produk>> {
        return Pager (config = PagingConfig(pageSize = 20, maxSize = 200),
                pagingSourceFactory = {ProductPagingSource(retroService)}).flow.cachedIn(viewModelScope)
    }

}