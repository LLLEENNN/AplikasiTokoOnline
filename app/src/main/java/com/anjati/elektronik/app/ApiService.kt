package com.anjati.elektronik.app

import com.anjati.elektronik.model.Chekout
import com.anjati.elektronik.model.ResponModel
import com.anjati.elektronik.model.rajaongkir.ResponOngkir
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("phone") nomortlp: String,
            @Field("password") password: String
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("check_email")
    fun checkEmail(
            @Field("email") email: String
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("reset_password")
    fun resetPassword(
            @Field("email") email: String,
            @Field("password") password: String
    ): Call<ResponModel>


    @FormUrlEncoded
    @POST("favorite")
    fun setFavorite(
            @Field("product_id") product_id: String,
            @Field("user_id") user_id: String
    ): Call<ResponModel>


    @GET("hapusfavorite")
    fun unFavorite(
            @Query("product_id") product_id: String,
            @Query("user_id") user_id: String
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("checkfavorite")
    fun checkFavorite(
            @Field("product_id") product_id: String,
            @Field("user_id") user_id: String
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ): Call<ResponModel>

    @POST("chekout")
    fun chekout(
            @Body data: Chekout
    ): Call<ResponModel>

    @GET("product")
    fun getProduk(): Call<ResponModel>

//    @GET("product")
//    suspend fun getProduct(@Query("page") query: Int): ProductResponse

    @GET("voucher")
    fun getVoucher(): Call<ResponModel>

    @GET("bank")
    fun getBank(): Call<ResponModel>

    @GET("service")
    fun getService(): Call<ResponModel>

    @GET("category")
    fun getCategory(): Call<ResponModel>

    @GET("slide")
    fun getPromo(): Call<ResponModel>

    @GET("province")
    fun getProvinsi(
            @Header("key") key: String
    ): Call<ResponModel>

    @GET("city")
    fun getKota(
            @Header("key") key: String,
            @Query("province") id: String
    ): Call<ResponModel>

    @GET("kecamatan")
    fun getKecamatan(
            @Query("id_kota") id: Int
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("cost")
    fun ongkir(
            @Header("key") key: String,
            @Field("origin") origin: String,
            @Field("destination") destination: String,
            @Field("weight") weight: Int,
            @Field("courier") courier: String
    ): Call<ResponOngkir>

    @GET("chekout/user/{id}")
    fun getRiwayat(
            @Path("id") id: Int
    ): Call<ResponModel>

    @GET("favorite/user/{id}")
    fun getFavorite(
            @Path("id") id: Int
    ): Call<ResponModel>

    @GET("product/search/{query}")
    fun getSearch(
            @Path("query") query: String
    ): Call<ResponModel>

    @GET("category/{kodeklmpk}")
    fun getProdukByCategory(
            @Path("kodeklmpk") query: String
    ): Call<ResponModel>

    @GET("promo/{check}")
    fun getPromoProduct(
            @Path("check") query: String
    ): Call<ResponModel>

    @GET("nonpromo/{check}")
    fun getProducts(
            @Path("check") query: String
    ): Call<ResponModel>

    @POST("chekout/cancel/{id}")
    fun batalChekout(
            @Path("id") id: Int
    ): Call<ResponModel>
}