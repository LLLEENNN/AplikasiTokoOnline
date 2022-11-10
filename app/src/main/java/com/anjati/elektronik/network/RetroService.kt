package com.anjati.elektronik.network

import retrofit2.http.GET
import retrofit2.http.Query

interface RetroService {

    @GET("getproduct")
    suspend fun getDataFromAPI(@Query("page") query: Int):ProductsResponse
}