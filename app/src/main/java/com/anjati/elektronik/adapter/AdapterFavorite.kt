package com.anjati.elektronik.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.anjati.elektronik.R
import com.anjati.elektronik.activity.DetailProdukActivity
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.model.Produk
import com.squareup.picasso.Picasso
import com.anjati.elektronik.model.Favorite
import kotlin.collections.ArrayList

class AdapterFavorite(var activity: Activity, var data: ArrayList<Favorite>) : RecyclerView.Adapter<AdapterFavorite.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_semua_produk, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val a = data[position]
        val status = a.product.stock
        Log.d("statusstok:", status.toString())
        var harga = Integer.valueOf(a.product.price)

        holder.tvNama.text = a.product.name
        holder.tvHarga.text = Helper().gantiRupiah(harga)
        if (status == "0"){
            holder.tvStatus.text = "Stok Habis"
            holder.tvStatus.setBackgroundResource(R.drawable.bg_btn_batal)
        } else {
            holder.tvStatus.text = "Tersedia"
            holder.tvStatus.setBackgroundResource(R.drawable.bg_btn_confirm)
        }
        val image = a.product.image
        Log.d("RESPONS", "image: "+ image)
        Picasso.get()
                .load(image)
                .into(holder.imgProduk)

        holder.layout.setOnClickListener {
            val activiti = Intent(activity, DetailProdukActivity::class.java)
            val str = Gson().toJson(a.product, Produk::class.java)
            activiti.putExtra("extra", str)
            activity.startActivity(activiti)
        }
    }

    fun addList(items: ArrayList<Favorite>){
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun clear(){
        data.clear()
        notifyDataSetChanged()
    }

}