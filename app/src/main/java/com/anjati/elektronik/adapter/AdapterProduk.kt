package com.anjati.elektronik.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
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
import kotlin.collections.ArrayList

class AdapterProduk(var activity: Activity, var data: ArrayList<Produk>) : RecyclerView.Adapter<AdapterProduk.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvDiskon = view.findViewById<TextView>(R.id.tvDiskon)
        val tvBerat = view.findViewById<TextView>(R.id.tv_berat)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val tvHargaPromo = view.findViewById<TextView>(R.id.tv_harga_promo)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]

        val hargaAsli = Integer.valueOf(a.price)
        var harga = Integer.valueOf(a.d_price)
        val diskon = a.discount.toString()
        val status = a.stock


        holder.tvHarga.text = Helper().gantiRupiah(hargaAsli)
        holder.tvHarga.paintFlags = holder.tvHarga.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.tvDiskon.text = diskon + "%"
        holder.tvNama.text = data[position].name
        holder.tvHargaPromo.text = Helper().gantiRupiah(harga)
        val image = data[position].image

        if (status == "0"){
            holder.tvStatus.text = "Stok Habis"
            holder.tvStatus.setBackgroundResource(R.drawable.bg_btn_batal)
        } else {
            holder.tvStatus.text = "Tersedia"
            holder.tvStatus.setBackgroundResource(R.drawable.bg_btn_confirm)
        }
        Log.d("RESPONS", "image: "+ image)
        Picasso.get()
                .load(image)
                .into(holder.imgProduk)

        holder.layout.setOnClickListener {
            val activiti = Intent(activity, DetailProdukActivity::class.java)
            val str = Gson().toJson(data[position], Produk::class.java)
            activiti.putExtra("extra", str)
            activity.startActivity(activiti)
        }
    }

}