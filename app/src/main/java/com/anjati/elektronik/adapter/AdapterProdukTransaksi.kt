package com.anjati.elektronik.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.anjati.elektronik.R
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.model.DetailTransaksi
import com.squareup.picasso.Picasso
import java.util.*

class AdapterProdukTransaksi(var data: ArrayList<DetailTransaksi>) : RecyclerView.Adapter<AdapterProdukTransaksi.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val tvWeight = view.findViewById<TextView>(R.id.tv_berat)
        val tvTotalHarga = view.findViewById<TextView>(R.id.tv_totalHarga)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_transaksi, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]

        val name = a.product.name
        holder.tvNama.text = name
        //holder.tvWeight.text = "Berat : " + a.product.weight
        //holder.tvHarga.text = Helper().gantiRupiah(a.product.price)

        holder.tvJumlah.text = a.total_item.toString() + " Items"
        val jumlah = Integer.valueOf(a.total_item)
        val stok = Integer.valueOf(a.product.stock)
        val harga = Integer.valueOf(a.product.price)
        val hgros1 = Integer.valueOf(a.product.hgros1)
        val hgros2 = Integer.valueOf(a.product.hgros2)
        val quantity1 = Integer.valueOf(a.product.quantity1)
        val quantity2 = Integer.valueOf(a.product.quantity2)
        val is_promo = a.product.is_promo
        val d_price = Integer.valueOf(a.product.d_price)
        val d_hgros1 = Integer.valueOf(a.product.d_hgros1)
        val d_hgros2 = Integer.valueOf(a.product.d_hgros2)

        val scope = quantity2 - 1

        if (is_promo.toLowerCase() == "ya"){
            when {
                jumlah < quantity1 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(d_price)
                    holder.tvTotalHarga.text = Helper().gantiRupiah(jumlah * d_price)
                }
                jumlah in quantity1..scope -> {
                    holder.tvHarga.text = Helper().gantiRupiah(d_hgros1)
                    holder.tvTotalHarga.text = Helper().gantiRupiah(jumlah * d_hgros1)
                }
                jumlah >= quantity2 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(d_hgros2)
                    holder.tvTotalHarga.text = Helper().gantiRupiah(jumlah * d_hgros2)
                }
            }
        } else {
            when {
                jumlah < quantity1 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(harga)
                    holder.tvTotalHarga.text = Helper().gantiRupiah(jumlah * harga)
                }
                jumlah in quantity1..scope -> {
                    holder.tvHarga.text = Helper().gantiRupiah(hgros1)
                    holder.tvTotalHarga.text = Helper().gantiRupiah(jumlah * hgros1)
                }
                jumlah >= quantity2 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(hgros2)
                    holder.tvTotalHarga.text = Helper().gantiRupiah(jumlah * hgros2)
                }
            }
        }




        holder.layout.setOnClickListener {
//            listener.onClicked(a)
        }

        val images = a.product.image
//        Log.d("Image :" , "url" + image)
        Picasso.get()
                .load(images)
                .error(R.drawable.bg_shimmer_poin)
                .into(holder.imgProduk)
    }

    interface Listeners {
        fun onClicked(data: DetailTransaksi)
    }



}