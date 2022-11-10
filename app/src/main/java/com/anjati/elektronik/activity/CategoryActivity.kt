package com.anjati.elektronik.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.anjati.elektronik.R
import com.anjati.elektronik.adapter.AdapterSemuaProduk
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.model.Category
import com.anjati.elektronik.model.Produk
import com.anjati.elektronik.model.ResponModel
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {

    lateinit var category: Category
    lateinit var rvCategory: RecyclerView
    lateinit var shimmer: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        shimmer = findViewById(R.id.shimmer_produk)
        rvCategory = findViewById(R.id.rv_category)
        getInfo()
    }

    private fun getInfo() {
        val data = intent.getStringExtra("extra")
        category = Gson().fromJson<Category>(data, Category::class.java)

        val kodeklmpk = category.kodeklmpk

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


//        titlep.text = "Menampilkan Produk berdasarkan kategori "+ category.namaklmpk
        // setToolbar
        Helper().setToolbar(this, toolbar, category.namaklmpk)
    }

    private var listProduk: ArrayList<Produk> = ArrayList()

    fun displayProduk() {
        val layoutManager4 = GridLayoutManager(this, 2)
        layoutManager4.orientation = GridLayoutManager.VERTICAL

        rvCategory.adapter = AdapterSemuaProduk(this, listProduk)
        rvCategory.layoutManager = layoutManager4

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        shimmer.startShimmer()
    }

    override fun onPause() {
        shimmer.stopShimmer()
        super.onPause()
    }
}