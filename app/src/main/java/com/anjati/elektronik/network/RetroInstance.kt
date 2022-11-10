package com.anjati.elektronik.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {

    companion object {
        val baseURL = "http://backend-toko.swalayannu.com/api/"

        fun getRetroInstance(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

        }
    }

}