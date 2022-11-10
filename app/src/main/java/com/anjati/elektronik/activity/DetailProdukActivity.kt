package com.anjati.elektronik.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.anjati.elektronik.R
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.room.MyDatabase
import com.squareup.picasso.Picasso
import com.anjati.elektronik.adapter.AdapterSemuaProduk
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.helper.SharedPref
import com.anjati.elektronik.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_produk.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_custom.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProdukActivity : AppCompatActivity() {

    lateinit var myDb: MyDatabase
    lateinit var produk: Produk
    lateinit var shimmer: ShimmerFrameLayout
    lateinit var rvCategory: RecyclerView

    lateinit var s: SharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)
        myDb = MyDatabase.getInstance(this)!! // call database
        s = SharedPref(this)

        shimmer = findViewById(R.id.shimmer_produk)
        rvCategory = findViewById(R.id.rv_category)



        getInfo()
        mainButton()
        checkKeranjang()
    }

    private fun checkFavorite() {
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson<Produk>(data, Produk::class.java)

        if (s.getStatusLogin()){

            val product_id = produk.id
            val user_id = SharedPref(this).getUser()!!.id

            ApiConfig.instanceRetrofit.checkFavorite(product_id.toString(), user_id.toString()).enqueue(object : Callback<ResponModel>{
                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                    val respon = response.body()!!
                    Log.d("Favorit:", ""+ response.body())
                    if (respon.success == 1) {
                        success("Produk Berhasil di Tambahkan ke Favoritmu!")
                        btn_favorit.visibility = View.GONE
                        btn_unfavorit.visibility = View.VISIBLE
                    } else {
                        error("Yuk, Tambahkan ke Produk Favoritmu!")
                        btn_favorit.visibility = View.VISIBLE
                        btn_unfavorit.visibility = View.GONE
                    }
                }
                override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
//        else {
//            val intent = Intent(this, MasukActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }


    }

    private fun mainButton() {
        btn_keranjang.setOnClickListener {

            val intentdata = intent.getStringExtra("extra")
            produk = Gson().fromJson<Produk>(intentdata, Produk::class.java)

            val status = produk.stock

            if (status == "0"){
                Toast.makeText(this,"Maaf, Stok Produk habis!", Toast.LENGTH_SHORT).show()
            } else {
                val data = myDb.daoKeranjang().getProduk(produk.id)
                if (data == null) {
                    insert()
                } else {
                    data.jumlah += 1
                    update(data)
                }
            }
        }

        btn_favorit.setOnClickListener {
            val intentdata = intent.getStringExtra("extra")
            produk = Gson().fromJson<Produk>(intentdata, Produk::class.java)
            if (s.getStatusLogin()){

                val loading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                loading.setTitleText("Loading..").setContentText("Proses menambah ke Daftar Favorite").show()

                val user_id = SharedPref(this).getUser()!!.id
                val product_id = produk.id

                ApiConfig.instanceRetrofit.setFavorite(product_id.toString(), user_id.toString()).enqueue(object : Callback<ResponModel> {
                    override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//                        loading.dismiss()
//                        error(t.message.toString())
                    }
                    override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                        loading.dismiss()
                        val respon = response.body()!!
                        Log.d("Favorit:", ""+ response.body())
                        if (respon.success == 1) {
                            successpertama("Berhasil menambahkan ke Favorit")
                            btn_favorit.visibility = View.GONE
                            btn_unfavorit.visibility = View.VISIBLE
                        } else {
                            error("Produk sudah di Tambahkan!")
                        }

                    }
                })
            } else {
                val intent = Intent(this, MasukActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }

        btn_unfavorit.setOnClickListener{
            val intentdata = intent.getStringExtra("extra")
            produk = Gson().fromJson<Produk>(intentdata, Produk::class.java)

            if (s.getStatusLogin()){

                val loading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                loading.setTitleText("Loading..").setContentText("Proses hapus dari Daftar Favorite").show()

                val user_id = SharedPref(this).getUser()!!.id
                val product_id = produk.id

                ApiConfig.instanceRetrofit.unFavorite(product_id.toString(), user_id.toString()).enqueue(object : Callback<ResponModel> {
                    override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//                        loading.dismiss()
//                        error(t.message.toString())
                    }
                    override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                        loading.dismiss()
                        val respon = response.body()!!
                        Log.d("Favorit:", ""+ response.body())
                        if (respon.success == 1) {
                            successpertama("Berhasil Terhapus")
                            btn_favorit.visibility = View.VISIBLE
                            btn_unfavorit.visibility = View.GONE
                        } else {
                            error("Produk sudah di Tambahkan!")
                        }

                    }
                })
            } else {
                val intent = Intent(this, MasukActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }


        btn_toKeranjang.setOnClickListener {
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            onBackPressed()
            finish()
        }
    }

    private fun insert() {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().insert(produk) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    checkKeranjang()
                    Log.d("respons", "data inserted")
                    Toast.makeText(this, "Berhasil menambah kekeranjang", Toast.LENGTH_SHORT).show()
                })
    }

    private fun update(data: Produk) {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().update(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    checkKeranjang()
                    Log.d("respons", "data inserted")
                    Toast.makeText(this, "Berhasil menambah kekeranjang", Toast.LENGTH_SHORT).show()
                })
    }

    private fun checkKeranjang() {
        val dataKranjang = myDb.daoKeranjang().getAll()

        if (dataKranjang.isNotEmpty()) {
            div_angka.visibility = View.VISIBLE
            tv_angka.text = dataKranjang.size.toString()
        } else {
            div_angka.visibility = View.GONE
        }
    }

    fun error(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("")
                .setContentText(pesan)
                .show()
    }

    fun success(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success..")
                .setContentText(pesan)
                .show()
    }

    fun successpertama(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success..")
                .setContentText(pesan)
                .show()
    }

    private fun getInfo() {
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson<Produk>(data, Produk::class.java)


//        val product_id = produk.id
//        val user_id = s.user_id
//        ApiConfig.instanceRetrofit.checkFavorite(product_id.toString(), user_id.toString()).enqueue(object : Callback<ResponModel>{
//            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
//                val respon = response.body()!!
//                Log.d("Favorit:", ""+ response.body())
//                if (respon.success == 1) {
//                    success("Cek Menu Favorite ya")
//                    btn_favorit.visibility = View.GONE
//                    btn_unfavorit.visibility = View.VISIBLE
//                } else {
//                    error("Produk belum favorit!")
//                    btn_favorit.visibility = View.VISIBLE
//                    btn_unfavorit.visibility = View.GONE
//                }
//            }
//            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })




        val status = produk.stock
//        val stok = Integer.valueOf(produk.stock)
//        val harga = Integer.valueOf(produk.price)
        val hgros1 = Integer.valueOf(produk.hgros1)
        val hgros2 = Integer.valueOf(produk.hgros2)
        val quantity1 = Integer.valueOf(produk.quantity1)
        val quantity2 = Integer.valueOf(produk.quantity2)
        val is_promo = produk.is_promo
        val d_price = Integer.valueOf(produk.d_price)
        val d_hgros1 = Integer.valueOf(produk.d_hgros1)
        val d_hgros2 = Integer.valueOf(produk.hgros2)



        pcsdua.text = produk.quantity1 + "pcs"
        pcstiga.text = produk.quantity2 + "pcs"

        if (is_promo.toLowerCase() == "ya"){
            val hitung1 = d_hgros1 * quantity1
            val hitung2 = d_hgros2 * quantity2

            toharga.text = Helper().gantiRupiah(produk.d_price)
            tohargadua.text = Helper().gantiRupiah(produk.d_hgros1)
            tohargatiga.text = Helper().gantiRupiah(produk.d_hgros2)

            stock.text = produk.stock
            kode_barang.text = produk.kodebrg
            harga_satu.text = Helper().gantiRupiah(produk.d_price)
            harga_dua.text = Helper().gantiRupiah(hitung1)
            harga_tiga.text = Helper().gantiRupiah(hitung2)

            // set Value
            tv_nama.text = produk.name
            tv_harga.text = Helper().gantiRupiah(produk.d_price)
            if (status == "0"){
                tv_stok.text = "Stok Habis"
                tv_stok.setBackgroundResource(R.drawable.bg_btn_batal)
            } else {
                tv_stok.text = "Tersedia"
                tv_stok.setBackgroundResource(R.drawable.bg_btn_confirm)
            }
        } else {
            val hitung1 = hgros1 * quantity1
            val hitung2 = hgros2 * quantity2

            toharga.text = Helper().gantiRupiah(produk.price)
            tohargadua.text = Helper().gantiRupiah(produk.hgros1)
            tohargatiga.text = Helper().gantiRupiah(produk.hgros2)

            stock.text = produk.stock
            kode_barang.text = produk.kodebrg
            harga_satu.text = Helper().gantiRupiah(produk.price)
            harga_dua.text = Helper().gantiRupiah(hitung1)
            harga_tiga.text = Helper().gantiRupiah(hitung2)

            // set Value
            tv_nama.text = produk.name
            tv_harga.text = Helper().gantiRupiah(produk.price)
            if (status == "0"){
                tv_stok.text = "Stok Habis"
                tv_stok.setBackgroundResource(R.drawable.bg_btn_batal)
            } else {
                tv_stok.text = "Tersedia"
                tv_stok.setBackgroundResource(R.drawable.bg_btn_confirm)
            }
        }
//        tv_deskripsi.text = produk.description

        val img = produk.image
        Picasso.get()
                .load(img)
                .resize(400, 400)
                .into(image)


        val kodeklmpk = produk.kodeklmpk
        val check = "tidak"
        ApiConfig.instanceRetrofit.getProdukByCategory(kodeklmpk.toLowerCase()).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                    rvCategory.visibility = View.VISIBLE
                    listProduk = res.product
                    displayProduk()
                }
            }
        })


        // setToolbar
        val nameProduct = produk.name
        val fix = nameProduct.substring(0,1)
        Helper().setToolbar(this, toolbar, nameProduct)
    }

    fun displayProduk() {
        val layoutManager4 = GridLayoutManager(this, 2)
        layoutManager4.orientation = GridLayoutManager.VERTICAL

        rvCategory.adapter = AdapterSemuaProduk(this, listProduk)
        rvCategory.layoutManager = layoutManager4

    }

    private var listProduk: ArrayList<Produk> = ArrayList()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        shimmer.startShimmer()
        checkFavorite()
        super.onResume()
    }

    override fun onPause() {
        shimmer.stopShimmer()
        super.onPause()
    }
}
