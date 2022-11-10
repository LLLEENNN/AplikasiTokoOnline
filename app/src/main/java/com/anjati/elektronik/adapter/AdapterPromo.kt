package com.anjati.elektronik.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.anjati.elektronik.R
import com.squareup.picasso.Picasso
import com.anjati.elektronik.model.Promo
import kotlin.collections.ArrayList

class AdapterPromo(var activity: Activity, var data: ArrayList<Promo>) : RecyclerView.Adapter<AdapterPromo.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val imagePromo = view.findViewById<ImageView>(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_promo, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val image = data[position].image

        Log.d("RESPONS", "image: "+ image)
        Picasso.get()
                .load(image)
                .into(holder.imagePromo)

//        holder.layout.setOnClickListener {
//            val activiti = Intent(activity, DetailProdukActivity::class.java)
//            val str = Gson().toJson(data[position], Produk::class.java)
//            activiti.putExtra("extra", str)
//            activity.startActivity(activiti)
//        }
    }

}