package com.anjati.elektronik.adapter

import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anjati.elektronik.R
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.model.Voucher

class AdapterVoucher(private val listVoucher: ArrayList<Voucher>) : RecyclerView.Adapter<AdapterVoucher.Holder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    lateinit var spanned: Spanned

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }


    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvType = view.findViewById<TextView>(R.id.tv_type)
        val tvMinOrder = view.findViewById<TextView>(R.id.tv_min_order)
        val desc = view.findViewById<TextView>(R.id.desc)
        val expired = view.findViewById<TextView>(R.id.expired)
        val btnKlaim = view.findViewById<TextView>(R.id.btn_klaim)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return listVoucher.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val a = listVoucher[position]

        val desc = a.descriptions
        val spanned = Html.fromHtml(desc)

        holder.tvType.text = a.type
        holder.expired.text = "Klaim voucher sebelum tanggal : " + a.expired
        holder.tvMinOrder.text = "Minimal Total Pembelian : " + Helper().gantiRupiah(a.min_order)
        holder.desc.text = spanned
        holder.btnKlaim.setOnClickListener { onItemClickCallback?.onItemClicked(a) }

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Voucher)
    }

}