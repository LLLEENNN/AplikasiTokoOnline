package com.anjati.elektronik.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.anjati.elektronik.R
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.model.Produk
import com.anjati.elektronik.room.MyDatabase
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class AdapterKeranjang(var activity: Activity, var data: ArrayList<Produk>, var listener: Listeners) : RecyclerView.Adapter<AdapterKeranjang.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)

        val btnTambah = view.findViewById<ImageView>(R.id.btn_tambah)
        val btnKurang = view.findViewById<ImageView>(R.id.btn_kurang)
        val btnDelete = view.findViewById<ImageView>(R.id.btn_delete)

        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val produk = data[position]
        val stok = Integer.valueOf(produk.stock)
        val harga = Integer.valueOf(produk.price)
        val hgros1 = Integer.valueOf(produk.hgros1)
        val hgros2 = Integer.valueOf(produk.hgros2)
        val quantity1 = Integer.valueOf(produk.quantity1)
        val quantity2 = Integer.valueOf(produk.quantity2)
        val is_promo = produk.is_promo
        val d_price = Integer.valueOf(produk.d_price)
        val d_hgros1 = Integer.valueOf(produk.d_hgros1)
        val d_hgros2 = Integer.valueOf(produk.d_hgros2)

        holder.tvNama.text = produk.name
        if (is_promo.toLowerCase() == "ya"){
            holder.tvHarga.text = Helper().gantiRupiah(d_price * produk.jumlah)
        } else {
            holder.tvHarga.text = Helper().gantiRupiah(harga * produk.jumlah)
        }

        holder.checkBox.isChecked = produk.selected
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            produk.selected = isChecked
            update(produk)
        }

        val image =  data[position].image
        Picasso.get()
                .load(image)
                .error(R.drawable.bg_shimmer_poin)
                .into(holder.imgProduk)

        var jumlah = data[position].jumlah
        holder.tvJumlah.text = jumlah.toString()

        val scope = quantity2 - 1

        if (is_promo.toLowerCase() == "ya"){
            when {
                jumlah < quantity1 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(d_price * jumlah)
                    produk.jumlah = jumlah
                    update(produk)
                    holder.tvJumlah.text = jumlah.toString()
                }
                jumlah in quantity1..scope -> {
                    holder.tvHarga.text = Helper().gantiRupiah(d_hgros1 * jumlah)
                    produk.jumlah = jumlah
                    update(produk)
                    holder.tvJumlah.text = jumlah.toString()
                }
                jumlah >= quantity2 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(d_hgros2 * jumlah)
                    produk.jumlah = jumlah
                    update(produk)
                    holder.tvJumlah.text = jumlah.toString()
                }
            }
        } else {
            when {
                jumlah < quantity1 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(harga * jumlah)
                    produk.jumlah = jumlah
                    update(produk)
                    holder.tvJumlah.text = jumlah.toString()
                }
                jumlah in quantity1..scope -> {
                    holder.tvHarga.text = Helper().gantiRupiah(hgros1 * jumlah)
                    produk.jumlah = jumlah
                    update(produk)
                    holder.tvJumlah.text = jumlah.toString()
                }
                jumlah >= quantity2 -> {
                    holder.tvHarga.text = Helper().gantiRupiah(hgros2 * jumlah)
                    produk.jumlah = jumlah
                    update(produk)
                    holder.tvJumlah.text = jumlah.toString()
                }
            }

        }

        holder.btnTambah.setOnClickListener {

            jumlah++

            if (jumlah == stok){
                Toast.makeText(activity, "Stok hanya tersedia : "+ stok +"!", Toast.LENGTH_SHORT).show()
                holder.btnTambah.visibility = View.GONE
            } else {
                holder.btnTambah.visibility = View.VISIBLE
            }

            val scope = quantity2 - 1

            if (is_promo.toLowerCase() == "ya"){
                when {
                    jumlah < quantity1 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(d_price * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah in quantity1..scope -> {
                        holder.tvHarga.text = Helper().gantiRupiah(d_hgros1 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah >= quantity2 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(d_hgros2 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                }
            } else {
                when {
                    jumlah < quantity1 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(harga * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah in quantity1..scope -> {
                        holder.tvHarga.text = Helper().gantiRupiah(hgros1 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah >= quantity2 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(hgros2 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                }
            }

        }

        holder.btnKurang.setOnClickListener {

            if (jumlah <= 1) return@setOnClickListener
            jumlah--

            if (jumlah == stok){
                Toast.makeText(activity, "Stok hanya tersedia :"+ stok, Toast.LENGTH_SHORT).show()
                holder.btnTambah.visibility = View.GONE
            } else {
                holder.btnTambah.visibility = View.VISIBLE
            }


            val scope = quantity2 - 1
            if (is_promo.toLowerCase() == "ya"){

                when {
                    jumlah < quantity1 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(d_price * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah in quantity1..scope -> {
                        holder.tvHarga.text = Helper().gantiRupiah(d_hgros1 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah >= quantity2 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(d_hgros2 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }

                }

            } else {
                when {
                    jumlah < quantity1 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(harga * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah in quantity1..scope -> {
                        holder.tvHarga.text = Helper().gantiRupiah(hgros1 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }
                    jumlah >= quantity2 -> {
                        holder.tvHarga.text = Helper().gantiRupiah(hgros2 * jumlah)
                        produk.jumlah = jumlah
                        update(produk)
                        holder.tvJumlah.text = jumlah.toString()
                    }

                }
            }


        }

        holder.btnDelete.setOnClickListener {
            delete(produk)
            listener.onDelete(position)
        }
    }

    interface Listeners {
        fun onUpdate()
        fun onDelete(position: Int)
    }

    private fun update(data: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().update(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    listener.onUpdate()
                })
    }

    private fun delete(data: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().delete(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //listener.onDelete(data.id)
                })
    }

}