package com.anjati.elektronik.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anjati.elektronik.R
import com.anjati.elektronik.activity.MasukActivity
import com.anjati.elektronik.activity.PengirimanActivity
import com.anjati.elektronik.adapter.AdapterKeranjang
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.helper.SharedPref
import com.anjati.elektronik.model.Produk
import com.anjati.elektronik.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * A simple [Fragment] subclass.
 */
class KeranjangFragment : Fragment() {

    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    // dipangil sekali ketika aktivity aktif
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_keranjang, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())

        mainButton()
        return view
    }

    lateinit var adapter: AdapterKeranjang
    var listProduk = ArrayList<Produk>()
    private fun displayProduk() {
        listProduk = myDb.daoKeranjang().getAll() as ArrayList
        if (listProduk.isEmpty()){
            tvTotal.text = "Rp.0"
        }
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterKeranjang(requireActivity(), listProduk, object : AdapterKeranjang.Listeners {
            override fun onUpdate() {
                hitungTotal()
            }

            override fun onDelete(position: Int) {
                listProduk.removeAt(position)
                adapter.notifyDataSetChanged()
                hitungTotal()
            }
        })
        rvProduk.adapter = adapter
        rvProduk.layoutManager = layoutManager
    }

    var totalHarga = 0
    var totalBerat = 0
    fun hitungTotal() {
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        if (listProduk.isEmpty()){
            tvTotal.text = "Rp.0"
        }
        totalHarga = 0
        totalBerat = 0
        var isSelectedAll = true
        for (produk in listProduk) {
            if (produk.selected) {
                val harga = Integer.valueOf(produk.price)
                val quantity1 = Integer.valueOf(produk.quantity1)
                val quantity2 = Integer.valueOf(produk.quantity2)
                val hgros1 = Integer.valueOf(produk.hgros1)
                val hgros2 = Integer.valueOf(produk.hgros2)
                val weight = Integer.valueOf(produk.weight)
                val jumlah = Integer.valueOf(produk.jumlah)
                val is_promo = produk.is_promo
                val d_price = Integer.valueOf(produk.d_price)
                val d_hgros1 = Integer.valueOf(produk.d_hgros1)
                val d_hgros2 = Integer.valueOf(produk.d_hgros2)

                if(is_promo.toLowerCase()=="ya"){
                    Log.d("Jumlah :" , jumlah.toString())

                    val scope = quantity2 - 1
                    when {
                        jumlah < quantity1 -> {
                            totalHarga += (d_price * jumlah)
                            totalBerat += (weight * jumlah)
                        }
                        jumlah in quantity1..scope -> {
                            totalHarga += (d_hgros1 * jumlah)
                            totalBerat += (weight * jumlah)
                        }
                        jumlah >= quantity2 -> {
                            totalHarga += (d_hgros2 * jumlah)
                            totalBerat += (weight * jumlah)
                        }
                    }
                } else {
                    Log.d("Jumlah :" , jumlah.toString())

                    val scope = quantity2 - 1
                    when {
                        jumlah < quantity1 -> {
                            totalHarga += (harga * jumlah)
                            totalBerat += (weight * jumlah)
                        }
                        jumlah in quantity1..scope -> {
                            totalHarga += (hgros1 * jumlah)
                            totalBerat += (weight * jumlah)
                        }
                        jumlah >= quantity2 -> {
                            totalHarga += (hgros2 * jumlah)
                            totalBerat += (weight * jumlah)
                        }
                    }
                }
            } else {
                isSelectedAll = false
               // tvTotal.text = Helper().gantiRupiah(totalHarga)
            }
        }
        cbAll.isChecked = isSelectedAll
        tvTotal.text = Helper().gantiRupiah(totalHarga)
    }

    private fun mainButton() {
        btnDelete.setOnClickListener {
            val listDelete = ArrayList<Produk>()
            for (p in listProduk) {
                if (p.selected) listDelete.add(p)
            }

            delete(listDelete)
        }

        btnBayar.setOnClickListener {

            if (s.getStatusLogin()) {
                var isThereProduk = false
                for (p in listProduk) {
                    if (p.selected) isThereProduk = true
                }

                if (isThereProduk) {
                    val intent = Intent(requireActivity(), PengirimanActivity::class.java)
                    intent.putExtra("extra", "" + totalHarga)
                    intent.putExtra("weight", "" + totalBerat)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Tidak ada produk yg terpilih", Toast.LENGTH_SHORT).show()
                }
            } else {
                requireActivity().startActivity(Intent(requireActivity(), MasukActivity::class.java))
            }
        }

        cbAll.setOnClickListener {
            for (i in listProduk.indices) {
                val produk = listProduk[i]
                produk.selected = cbAll.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun delete(data: ArrayList<Produk>) {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().delete(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    listProduk.clear()
                    tvTotal.text = "Rp.0"
                    listProduk.addAll(myDb.daoKeranjang().getAll() as ArrayList)
                    adapter.notifyDataSetChanged()
                })
    }

    lateinit var btnDelete: ImageView
    lateinit var rvProduk: RecyclerView
    lateinit var tvTotal: TextView
    lateinit var btnBayar: TextView
    lateinit var cbAll: CheckBox
    private fun init(view: View) {
        btnDelete = view.findViewById(R.id.btn_delete)
        rvProduk = view.findViewById(R.id.rv_produk)
        tvTotal = view.findViewById(R.id.tv_total)
        btnBayar = view.findViewById(R.id.btn_bayar)
        cbAll = view.findViewById(R.id.cb_all)
    }

    override fun onResume() {
        displayProduk()
        hitungTotal()
        super.onResume()
    }


}
