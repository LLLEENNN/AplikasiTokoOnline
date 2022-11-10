package com.anjati.elektronik.network

import com.anjati.elektronik.model.Produk

data class ProductsResponse(
        val prev_page_url: String?,
        val next_page_url: String?,
        val data: List<Produk>
)