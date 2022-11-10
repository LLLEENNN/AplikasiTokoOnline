package com.anjati.elektronik.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.anjati.elektronik.R
import com.squareup.picasso.Picasso
import com.anjati.elektronik.activity.CategoryActivity
import com.anjati.elektronik.model.Category
import kotlin.collections.ArrayList

class AdapterMenu(var activity: Activity, var data: ArrayList<Category>) : RecyclerView.Adapter<AdapterMenu.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val icon = view.findViewById<ImageView>(R.id.icon)
        val title = view.findViewById<TextView>(R.id.title)
        val layout = view.findViewById<RelativeLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val image = data[position].image
        holder.title.text = data[position].namaklmpk
        Log.d("RESPONS", "image: "+ image)
        Picasso.get()
                .load(image)
                .error(R.drawable.semua)
                .into(holder.icon)


        holder.layout.setOnClickListener {
            val activiti = Intent(activity, CategoryActivity::class.java)
            val str = Gson().toJson(data[position], Category::class.java)
            activiti.putExtra("extra", str)
            activity.startActivity(activiti)
        }
    }

}