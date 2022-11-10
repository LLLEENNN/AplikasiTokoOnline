package com.anjati.elektronik.model

class ResponModel {
    var success = 0
    lateinit var message: String
    var user = User()
    var favorited = Favorite()

    var product: ArrayList<Produk> = ArrayList()

    var voucher: ArrayList<Voucher> = ArrayList()
    var service: ArrayList<Nomor> = ArrayList()

    var bank: ArrayList<Bank> = ArrayList()
    var transactions: ArrayList<Transaksi> = ArrayList()

    var favorite : ArrayList<Favorite> = ArrayList()

    var category: ArrayList<Category> = ArrayList()
    var slide: ArrayList<Promo> = ArrayList()

    var rajaongkir = ModelAlamat()
    var transaksi = Transaksi()

    var provinsi: ArrayList<ModelAlamat> = ArrayList()
    var kota_kabupaten: ArrayList<ModelAlamat> = ArrayList()
    var kecamatan: ArrayList<ModelAlamat> = ArrayList()
}