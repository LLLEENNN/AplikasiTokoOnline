package com.anjati.elektronik.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.anjati.elektronik.R
import com.anjati.elektronik.adapter.*
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.model.*
import com.anjati.elektronik.room.MyDatabase
import com.anjati.elektronik.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
//import kotlinx.android.synthetic.main.fragment_home.checkVoucher
import kotlinx.coroutines.flow.collectLatest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var rvSemuaProduk: RecyclerView
    lateinit var rvProductPromo: RecyclerView
//    lateinit var rvPromo: RecyclerView
//    lateinit var rvVoucher: RecyclerView
    lateinit var rvMenu: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var myDb: MyDatabase
    lateinit var shimmerSemua: ShimmerFrameLayout
//    lateinit var shimmerVoucher: ShimmerFrameLayout
//    lateinit var shimmerPromo: ShimmerFrameLayout
    lateinit var shimmerMenu: ShimmerFrameLayout
    lateinit var shimmerProductPromo: ShimmerFrameLayout
    lateinit var div_alamat: RelativeLayout
    lateinit var alamat: TextView
    lateinit var titlePromo: TextView
    lateinit var div_kranjang: RelativeLayout
    lateinit var tv_number: TextView


    private lateinit var adapterProduct: AdapterProduct
    private lateinit var gridLayoutManager: GridLayoutManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!! // call database
        getPromo()
        getMenu()
//        getVoucher()
        getProductPromo()
        getSemuaProduk()


        return view
    }

    private fun getProductPromo() {
        val check = "YA"
        ApiConfig.instanceRetrofit.getPromoProduct(check.toLowerCase()).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.product.isNotEmpty()) {
                    shimmerProductPromo.stopShimmer()
                    titlePromo.visibility = View.VISIBLE
                    shimmerProductPromo.visibility = View.GONE
                    rvProductPromo.visibility = View.VISIBLE
                    listPromoProduct = res.product
                    displayProduk()
                } else {
                    shimmerProductPromo.stopShimmer()
                    titlePromo.visibility = View.GONE
                    shimmerProductPromo.visibility = View.GONE
                    checkPromo.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun getVoucher() {
        ApiConfig.instanceRetrofit.getVoucher().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.voucher.isNotEmpty()) {
//                    shimmerVoucher.stopShimmer()
//                    shimmerVoucher.visibility = View.GONE
//                    rvVoucher.visibility = View.VISIBLE
                    listVoucher = res.voucher
                    displayProduk()
                } else {
//                    checkVoucher.visibility = View.VISIBLE
//                    shimmerVoucher.stopShimmer()
//                    shimmerVoucher.visibility = View.GONE
                }
            }
        })
    }

    //
    private fun setupRecyclerView() {
        gridLayoutManager = GridLayoutManager(context, 2)
        rvSemuaProduk.apply {
            rvSemuaProduk.layoutManager = gridLayoutManager
            adapterProduct = AdapterProduct()
            adapter = adapterProduct
        }
    }

    private fun initViewModel() {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        lifecycleScope.launchWhenCreated {
            homeViewModel.getListData().collectLatest {
                adapterProduct.submitData(it)
                shimmerSemua.visibility = View.GONE
            }
        }
    }


    fun chekAlamat() {

        if (myDb.daoAlamat().getByStatus(true) != null) {
            div_alamat.visibility = View.VISIBLE
            val a = myDb.daoAlamat().getByStatus(true)!!
            alamat.text = a.type + " " + a.name
        } else {
            div_alamat.visibility = View.GONE

        }
    }


    private fun getMenu() {
        ApiConfig.instanceRetrofit.getCategory().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    shimmerMenu.stopShimmer()
                    shimmerMenu.visibility = View.GONE
                    rvMenu.visibility = View.VISIBLE
                    listMenu = res.category
                    displayProduk()
                }
            }
        })
    }

    private fun getPromo() {
        ApiConfig.instanceRetrofit.getPromo().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
//                    shimmerPromo.stopShimmer()
//                    shimmerPromo.visibility = View.GONE
//                    rvPromo.visibility = View.VISIBLE
                    listPromo = res.slide
                    displayProduk()
                }
            }
        })
    }

    fun displayProduk() {
//        Log.d("cekini", "size:" + listProduk.size)
//
        val layoutManager4 = GridLayoutManager(activity, 2)
        layoutManager4.orientation = GridLayoutManager.VERTICAL

        val layoutManager3 = LinearLayoutManager(activity)
        layoutManager3.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        val layoutVoucher = LinearLayoutManager(activity)
        layoutVoucher.orientation = LinearLayoutManager.HORIZONTAL

        val layoutPromoProduct = LinearLayoutManager(activity)
        layoutPromoProduct.orientation = LinearLayoutManager.HORIZONTAL

//        rvPromo.adapter = AdapterPromo(requireActivity(), listPromo)
//        rvPromo.layoutManager = layoutManager3

        rvMenu.adapter = AdapterMenu(requireActivity(), listMenu)
        rvMenu.layoutManager = layoutManager2

//        rvVoucher.adapter = AdapterVoucherHome(requireActivity(), listVoucher)
//        rvVoucher.layoutManager = layoutVoucher

        rvSemuaProduk.adapter = AdapterSemuaProduk(requireActivity(), listProduk)
        rvSemuaProduk.layoutManager = layoutManager4

        rvProductPromo.adapter = AdapterProduk(requireActivity(), listPromoProduct)
        rvProductPromo.layoutManager = layoutPromoProduct

    }

    private var listProduk: ArrayList<Produk> = ArrayList()
    private var listPromoProduct: ArrayList<Produk> = ArrayList()
    private var listVoucher: ArrayList<Voucher> = ArrayList()
    private var listPromo: ArrayList<Promo> = ArrayList()
    private var listMenu: ArrayList<Category> = ArrayList()

    fun getSemuaProduk() {
        val check = "TIDAK"
        ApiConfig.instanceRetrofit.getProducts(check.toLowerCase()
        ).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    shimmerSemua.stopShimmer()
                    shimmerSemua.visibility = View.GONE
                    rvSemuaProduk.visibility = View.VISIBLE
                    listProduk = res.product
                    displayProduk()
                }
            }
        })
    }


    fun init(view: View) {

        shimmerSemua = view.findViewById(R.id.shimmer_produk)
//        shimmerVoucher = view.findViewById(R.id.shimmer_voucher)
        shimmerProductPromo = view.findViewById(R.id.shimmer_produk_promo)
//        shimmerPromo = view.findViewById(R.id.shimmer_promo)
        shimmerMenu = view.findViewById(R.id.shimmer_menu)
//        rvVoucher = view.findViewById(R.id.rv_voucher)
        rvProductPromo = view.findViewById(R.id.rv_promo_product)
        rvSemuaProduk = view.findViewById(R.id.rv_semua_produk)
//        rvPromo = view.findViewById(R.id.rv_promo)
        rvMenu = view.findViewById(R.id.rv_menu)
        alamat = view.findViewById(R.id.alamat)
        div_alamat = view.findViewById(R.id.div_alamat)
        titlePromo = view.findViewById(R.id.titlePromo)
    }



    override fun onResume() {
        super.onResume()
        shimmerSemua.startShimmer()
        shimmerMenu.startShimmer()
//        shimmerVoucher.startShimmer()
//        shimmerPromo.startShimmer()
        shimmerProductPromo.startShimmer()
        chekAlamat()
    }

    override fun onPause() {
//        shimmerVoucher.stopShimmer()
        shimmerProductPromo.stopShimmer()
        shimmerSemua.stopShimmer()
//        shimmerPromo.stopShimmer()
        shimmerMenu.stopShimmer()
        super.onPause()
    }

}
