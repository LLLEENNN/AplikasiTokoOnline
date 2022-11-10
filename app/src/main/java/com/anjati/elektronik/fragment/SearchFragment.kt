package com.anjati.elektronik.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.anjati.elektronik.R
import com.anjati.elektronik.adapter.AdapterSemuaProduk
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.helper.SharedPref
import com.anjati.elektronik.model.Produk
import com.anjati.elektronik.model.ResponModel
import com.anjati.elektronik.room.MyDatabase
import kotlinx.android.synthetic.main.fragment_help.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    lateinit var rvSemuaProduk: RecyclerView
//    lateinit var btn_search: ImageView
//    lateinit var tv_search: TextView
    lateinit var searchView: SearchView

    lateinit var shimmerSemua: ShimmerFrameLayout

    // dipangil sekali ketika aktivity aktif
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_help, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())

//        btn_search.setOnClickListener(View.OnClickListener {
//            getSearchProduk()
//        })
        searchView.queryHint = "Cari produk disini"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val text = query
                ApiConfig.instanceRetrofit.getSearch(text.toLowerCase()).enqueue(object : Callback<ResponModel> {
                    override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                        shimmerSemua.stopShimmer()
                        error(t.message.toString())
                    }

                    override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                        val res = response.body()!!
                        if (res.success == 1) {
                            shimmerSemua.stopShimmer()
                            shimmerSemua.visibility = View.GONE
                            rvSemuaProduk.visibility = View.VISIBLE
                            listProduk = res.product
                            displayProduk()
                        } else if (res.product == null) {
                            error(res.message)
                            tv_search.visibility = View.VISIBLE
                        }
                    }
                })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

        })

        getSemuaProduk()

        return view
    }

//    fun getSearchProduk() {
//        shimmerSemua.startShimmer()
//        val query = edt_search.text.toString()
//        if (query.isEmpty()){
//            Toast.makeText(context, "Masukan kata kunci", Toast.LENGTH_SHORT).show()
//            edt_search.requestFocus()
//            getSemuaProduk()
//        } else {
//            ApiConfig.instanceRetrofit.getSearch(query.toLowerCase()).enqueue(object : Callback<ResponModel> {
//                override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//                    shimmerSemua.stopShimmer()
//                    error(t.message.toString())
//                }
//
//                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
//                    val res = response.body()!!
//                    if (res.success == 1) {
//                        shimmerSemua.stopShimmer()
//                        shimmerSemua.visibility = View.GONE
//                        rvSemuaProduk.visibility = View.VISIBLE
//                        listProduk = res.product
//                        displayProduk()
//                    } else if (res.product == null) {
//                        error(res.message)
//                        tv_search.visibility = View.VISIBLE
//                    }
//                }
//            })
//        }
//    }

    fun init(view: View) {
        shimmerSemua = view.findViewById(R.id.shimmer_produk)
        rvSemuaProduk = view.findViewById(R.id.rv_semua_produk)
//        btn_search = view.findViewById(R.id.btn_search)
//        tv_search = view.findViewById(R.id.tv_search)
        searchView = view.findViewById(R.id.searchView)
    }

    private var listProduk: ArrayList<Produk> = ArrayList()

    fun getSemuaProduk() {
        ApiConfig.instanceRetrofit.getProduk().enqueue(object : Callback<ResponModel> {
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

    fun displayProduk() {

        val layoutManager4 = GridLayoutManager(activity, 2)
        layoutManager4.orientation = GridLayoutManager.VERTICAL

        rvSemuaProduk.adapter = AdapterSemuaProduk(requireActivity(), listProduk)
        rvSemuaProduk.layoutManager = layoutManager4


    }

}
