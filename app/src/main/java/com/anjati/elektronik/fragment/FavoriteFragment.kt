package com.anjati.elektronik.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout

import com.anjati.elektronik.R
import com.anjati.elektronik.activity.MasukActivity
import com.anjati.elektronik.adapter.AdapterFavorite
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.helper.SharedPref
import com.anjati.elektronik.model.Favorite
import com.anjati.elektronik.model.ResponModel
import com.anjati.elektronik.room.MyDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {

    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    lateinit var rvFavorite: RecyclerView

    lateinit var shimmerFavorite: ShimmerFrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_favorite, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())

        getFavorite()

        return view
    }

    fun getFavorite() {
        if (s.getStatusLogin()){
            val id = SharedPref(requireActivity()).getUser()!!.id
            ApiConfig.instanceRetrofit.getFavorite(id).enqueue(object : Callback<ResponModel> {
                override fun onFailure(call: Call<ResponModel>, t: Throwable) {

                }
                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                    val res = response.body()!!
                    Log.d("Favorite", res.toString())
                    if (res.success == 1) {
                        shimmerFavorite.stopShimmer()
                        shimmerFavorite.visibility = View.GONE
                        rvFavorite.visibility = View.VISIBLE
                        listProduk = res.favorite
                        displayProduk()
                    }
                }
            })
        } else {
            val intent = Intent(requireActivity(), MasukActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    fun init(view: View) {
        shimmerFavorite = view.findViewById(R.id.shimmer_favorite)
        rvFavorite = view.findViewById(R.id.rv_favorite)
    }

    private var listProduk: ArrayList<Favorite> = ArrayList()

    fun displayProduk() {

        val layoutManager4 = GridLayoutManager(activity,2)
        layoutManager4.orientation = GridLayoutManager.VERTICAL

        rvFavorite.adapter = AdapterFavorite(requireActivity(), listProduk)
        rvFavorite.layoutManager = layoutManager4

    }

    override fun onPause() {
        shimmerFavorite.stopShimmer()
        super.onPause()
    }

    override fun onResume() {
        shimmerFavorite.startShimmer()
        super.onResume()
    }

}
