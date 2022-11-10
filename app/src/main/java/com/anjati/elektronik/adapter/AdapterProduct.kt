package com.anjati.elektronik.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.anjati.elektronik.R
import com.anjati.elektronik.model.Produk

class AdapterProduct() : PagingDataAdapter<Produk, AdapterProduct.ViewHolder>(DiffUtilCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(getItem(position)!!)
        val review = getItem(position)

        if (review != null) {
            holder.bind(review, position)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_semua_produk, parent, false)
        return ViewHolder(inflater)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)

        fun bind(data : Produk, position: Int){
            tvNama.text = data.name
            tvHarga.text = data.price
            Picasso.get()
                    .load(data.image)
                    .into(imgProduk)
        }
    }

    class DiffUtilCallback: DiffUtil.ItemCallback<Produk>(){
        override fun areItemsTheSame(oldItem: Produk, newItem: Produk): Boolean {
           return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Produk, newItem: Produk): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.price == newItem.price
        }

    }


}