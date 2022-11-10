package com.anjati.elektronik.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.anjati.elektronik.R
import com.anjati.elektronik.adapter.AdapterKurir
import com.anjati.elektronik.adapter.AdapterVoucher
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.app.ApiConfigAlamat
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.helper.SharedPref
import com.anjati.elektronik.model.Chekout
import com.anjati.elektronik.model.ResponModel
import com.anjati.elektronik.model.Voucher
import com.anjati.elektronik.model.rajaongkir.Costs
import com.anjati.elektronik.model.rajaongkir.ResponOngkir
import com.anjati.elektronik.room.MyDatabase
import com.anjati.elektronik.util.ApiKey
import kotlinx.android.synthetic.main.activity_pengiriman.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengirimanActivity : AppCompatActivity() {

    lateinit var myDb: MyDatabase
    var totalHarga = 0
    var totalBerat = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengiriman)
        Helper().setToolbar(this, toolbar, "Pengiriman")
        myDb = MyDatabase.getInstance(this)!!

        totalHarga = Integer.valueOf(intent.getStringExtra("extra")!!)
        tv_totalBelanja.text = Helper().gantiRupiah(totalHarga)
//        tv_ongkir.text = Helper().gantiRupiah(ongkir)
//        tv_total.text = Helper().gantiRupiah(Integer.valueOf(ongkir) + totalHarga)

        chekAlamat()
        mainButton()
        setSepiner()
    }

//    private fun getVoucher() {

//        ApiConfig.instanceRetrofit.getVoucher().enqueue(object : Callback<ResponModel> {
//            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
//            }
//            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
//                val res = response.body()!!
//                Log.d("Voucher :", res.toString())
//                if (res.voucher.isNotEmpty()) {
//                    rv_voucher.visibility = View.VISIBLE
////                    tv_gratis_ongkir.visibility = View.VISIBLE
////                    checkVoucher.visibility = View.GONE
//                    listVoucher = res.voucher
//                    displayVoucher()
//                } else {
////                    tv_gratis_ongkir.visibility = View.GONE
////                    checkVoucher.visibility = View.VISIBLE
//                }
//            }
//        })
//    }
//
//    private var listVoucher: ArrayList<Voucher> = ArrayList()
//
//    private fun displayVoucher() {
//
//        val voucherAdapter = AdapterVoucher(listVoucher)
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        rv_voucher.adapter = voucherAdapter
//        rv_voucher.layoutManager = layoutManager
//        voucherAdapter.setOnItemClickCallback(object : AdapterVoucher.OnItemClickCallback{
//            override fun onItemClicked(data: Voucher) {
//                if(Integer.valueOf(totalHarga) >= Integer.valueOf(data.min_order)){
//                    ongkir = Integer.valueOf(0).toString()
//                    jasaKirim = "0"
//                    setTotal(ongkir)
//                } else {
//                    Toast.makeText(this@PengirimanActivity, "Maaf Total belanja kamu masih kurang dari "+ Helper().gantiRupiah(data.min_order) , Toast.LENGTH_SHORT).show()
//                }
//            }
//        });
//    }

    fun setSepiner() {


        val arryString = ArrayList<String>()

        if (myDb.daoAlamat().getByStatus(true) != null) {
            div_alamat.visibility = View.VISIBLE
            div_kosong.visibility = View.GONE
            div_metodePengiriman.visibility = View.VISIBLE

            val a = myDb.daoAlamat().getByStatus(true)!!
            tv_nama.text = a.name
            tv_phone.text = a.phone
            tv_alamat.text = a.alamat + ", " + a.kota + ", " + a.kodepos + ", (" + a.type + ")"

            btn_tambahAlamat.text = "Ubah Alamat"
        } else {
            div_alamat.visibility = View.GONE
            div_kosong.visibility = View.VISIBLE
            btn_tambahAlamat.text = "Tambah Alamat"
        }

        arryString.add("JNE")
        arryString.add("POS")
        arryString.add("TIKI")

        val adapter = ArrayAdapter<Any>(this, R.layout.item_spinner, arryString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.setNotifyOnChange(true)
        spn_kurir.adapter = adapter
        spn_kurir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val kurirq = spn_kurir.selectedItem.toString()
                    if (kurirq == "COD"){
                        if (totalHarga <= 200000){
                            val jasa = 12000
                            ongkir = jasa.toString()
                        } else {
                            ongkir = Integer.valueOf(totalHarga * 3 / 100).toString()
                        }
                        kurir = kurirq
                        jasaKirim = "0"
                        setTotal(ongkir)
                        rv_metode.visibility = View.GONE
                        mkirim.visibility = View.GONE
//                        tv_gratis_ongkir.visibility = View.VISIBLE
//                        getVoucher()
                    } else {
                        getOngkir(spn_kurir.selectedItem.toString())
                        rv_metode.visibility = View.VISIBLE
//                        rv_voucher.visibility = View.GONE
//                        tv_gratis_ongkir.visibility = View.GONE
                        mkirim.visibility = View.VISIBLE
//                        tv_gratis_ongkir.visibility = View.VISIBLE
//                        getVoucher()
                        //mkirim.visibility = View.GONE
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun chekAlamat() {

        if (myDb.daoAlamat().getByStatus(true) != null) {
            div_alamat.visibility = View.VISIBLE
            div_kosong.visibility = View.GONE
            div_metodePengiriman.visibility = View.VISIBLE

            val a = myDb.daoAlamat().getByStatus(true)!!
            tv_nama.text = a.name
            tv_phone.text = a.phone
            tv_alamat.text = a.alamat + ", " + a.kota + ", " + a.kodepos + ", (" + a.type + ")"
            //btn_tambahAlamat.text = "Ubah Alamat"
//            val region = a.kota
//            val selected = a.isSelected
//            if (region == "Bojonegoro") {
//                if (selected.toString() == "1") {
//                    getVoucher()
//                }
//            }

            getOngkir("JNE")
        } else {
            div_alamat.visibility = View.GONE
            div_kosong.visibility = View.VISIBLE
            div_metodePengiriman.visibility = View.GONE
            btn_tambahAlamat.text = "Tambah Alamat"
        }
    }

    private fun mainButton() {
        btn_tambahAlamat.setOnClickListener {
            startActivity(Intent(this, ListAlamatActivity::class.java))
        }

        btn_bayar.setOnClickListener {
            if (myDb.daoAlamat().getByStatus(true) != null){
                bayar()
            } else {
                Toast.makeText(this, "Tambahkan Alamat Terlebih dahulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bayar() {
        val user = SharedPref(this).getUser()!!
        val a = myDb.daoAlamat().getByStatus(true)!!

        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        var totalItem = 0
        var totalHarga = 0
        var totalBerat = 0
        val products = ArrayList<Chekout.Item>()
        for (p in listProduk) {
            totalItem += Integer.valueOf(p.jumlah)
            if (p.selected) {
               if (p.is_promo.toLowerCase() == "ya"){
                   when {
                       totalItem < Integer.valueOf(p.quantity1) -> {
                           totalHarga += (p.jumlah * Integer.valueOf(p.d_price))
                           totalBerat += Integer.valueOf(p.weight)
                           val produk = Chekout.Item()
                           var catatanPesanan = if (edt_catatan.text.isEmpty()) "Tidak Ada Catatan" else edt_catatan.text.toString()
                           produk.id = "" + p.id
                           produk.total_item = "" + p.jumlah
                           produk.total_harga = "" + (p.jumlah * Integer.valueOf(p.d_price))
                           produk.catatan = catatanPesanan
                           products.add(produk)
                       }
                       totalItem in Integer.valueOf(p.quantity1)..Integer.valueOf(p.quantity2)-1 -> {
                           totalHarga += (p.jumlah * Integer.valueOf(p.d_hgros1))
                           totalBerat += Integer.valueOf(p.weight)
                           val produk = Chekout.Item()
                           var catatanPesanan = if (edt_catatan.text.isEmpty()) "Tidak Ada Catatan" else edt_catatan.text.toString()
                           produk.id = "" + p.id
                           produk.total_item = "" + p.jumlah
                           produk.total_harga = "" + (p.jumlah * Integer.valueOf(p.d_hgros1))
                           produk.catatan = catatanPesanan
                           products.add(produk)
                       }

                       totalItem >= Integer.valueOf(p.quantity2) -> {
                           totalHarga += (p.jumlah * Integer.valueOf(p.d_hgros2))
                           totalBerat += Integer.valueOf(p.weight)
                           val produk = Chekout.Item()
                           var catatanPesanan = if (edt_catatan.text.isEmpty()) "Tidak Ada Catatan" else edt_catatan.text.toString()
                           produk.id = "" + p.id
                           produk.total_item = "" + p.jumlah
                           produk.total_harga = "" + p.jumlah * Integer.valueOf(p.d_hgros2)
                           produk.catatan = catatanPesanan
                           products.add(produk)
                       }
                   }
               } else {
                   when {
                       totalItem < Integer.valueOf(p.quantity1) -> {
                           totalHarga += (p.jumlah * Integer.valueOf(p.price))
                           totalBerat += Integer.valueOf(p.weight)
                           val produk = Chekout.Item()
                           var catatanPesanan = if (edt_catatan.text.isEmpty()) "Tidak Ada Catatan" else edt_catatan.text.toString()
                           produk.id = "" + p.id
                           produk.total_item = "" + p.jumlah
                           produk.total_harga = "" + (p.jumlah* Integer.valueOf(p.price))
                           produk.catatan = catatanPesanan
                           products.add(produk)
                       }
                       totalItem in Integer.valueOf(p.quantity1)..Integer.valueOf(p.quantity2)-1 -> {
                           totalHarga += (p.jumlah * Integer.valueOf(p.hgros1))
                           totalBerat += Integer.valueOf(p.weight)
                           val produk = Chekout.Item()
                           var catatanPesanan = if (edt_catatan.text.isEmpty()) "Tidak Ada Catatan" else edt_catatan.text.toString()
                           produk.id = "" + p.id
                           produk.total_item = "" + Integer.valueOf(p.jumlah)
                           produk.total_harga = "" + (Integer.valueOf(p.jumlah) * Integer.valueOf(p.hgros1))
                           produk.catatan = catatanPesanan
                           products.add(produk)
                       }

                       totalItem >= Integer.valueOf(p.quantity2) -> {
                           totalHarga += (p.jumlah * Integer.valueOf(p.hgros2))
                           totalBerat += Integer.valueOf(p.weight)
                           val produk = Chekout.Item()
                           var catatanPesanan = if (edt_catatan.text.isEmpty()) "Tidak Ada Catatan" else edt_catatan.text.toString()
                           produk.id = "" + p.id
                           produk.total_item = "" + Integer.valueOf(p.jumlah)
                           produk.total_harga = "" + (Integer.valueOf(p.jumlah) * Integer.valueOf(p.hgros2))
                           produk.catatan = catatanPesanan
                           products.add(produk)
                       }
                   }
               }
            }
        }

        val chekout = Chekout()
        chekout.user_id = "" + user.id
        chekout.total_item = "" + totalItem
        chekout.total_harga = "" + totalHarga
        chekout.name = a.name
        chekout.phone = a.phone
        chekout.jasa_pengiriman = jasaKirim
        chekout.ongkir = ongkir
        chekout.kurir = kurir
        chekout.detail_lokasi = tv_alamat.text.toString()
        chekout.total_transfer = "" + (totalHarga + Integer.valueOf(ongkir))
        chekout.products = products

        val json = Gson().toJson(chekout, Chekout::class.java)
        Log.d("Respon:", "json:" + json)
        val intent = Intent(this, PembayaranActivity::class.java)
        intent.putExtra("extra", json)
        startActivity(intent)
    }

    private fun getOngkir(kurir: String) {

        val alamat = myDb.daoAlamat().getByStatus(true)
        totalBerat = Integer.valueOf(intent.getStringExtra("weight")!!)

        val origin = "169"
        val destination = "" + alamat!!.id_kota.toString()
        val berat = totalBerat

        ApiConfigAlamat.instanceRetrofit.ongkir(ApiKey.key, origin, destination, berat, kurir.toLowerCase()).enqueue(object : Callback<ResponOngkir> {
            override fun onResponse(call: Call<ResponOngkir>, response: Response<ResponOngkir>) {
                if (response.isSuccessful) {
                    Log.d("Success", "berhasil memuat data")
                    val result = response.body()!!.rajaongkir.results
                    if (result.isNotEmpty()) {
                        displayOngkir(result[0].code.toUpperCase(), result[0].costs)
                    }
                } else {
                    Log.d("Error", "gagal memuat data:" + response.message())
                }
            }

            override fun onFailure(call: Call<ResponOngkir>, t: Throwable) {
                Log.d("Error", "gagal memuat data:" + t.message)
            }

        })

    }

    var ongkir = ""
    var kurir = ""
    var jasaKirim = ""
    private fun displayOngkir(_kurir: String, arrayList: ArrayList<Costs>) {

        var arrayOngkir = ArrayList<Costs>()
        for (i in arrayList.indices) {
            val ongkir = arrayList[i]
            if (i == 0) {
                ongkir.isActive = true
            }
            arrayOngkir.add(ongkir)
        }
        setTotal(arrayOngkir[0].cost[0].value)
        ongkir = arrayOngkir[0].cost[0].value
        kurir = _kurir
        jasaKirim = arrayOngkir[0].service

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        var adapter: AdapterKurir? = null
        adapter = AdapterKurir(arrayOngkir, _kurir, object : AdapterKurir.Listeners {
            override fun onClicked(data: Costs, index: Int) {
                val newArrayOngkir = ArrayList<Costs>()
                for (ongkir in arrayOngkir) {
                    ongkir.isActive = data.description == ongkir.description
                    newArrayOngkir.add(ongkir)
                }
                arrayOngkir = newArrayOngkir
                adapter!!.notifyDataSetChanged()
                setTotal(data.cost[0].value)

                ongkir = data.cost[0].value
                kurir = _kurir
                jasaKirim = data.service
            }
        })
        rv_metode.adapter = adapter
        rv_metode.layoutManager = layoutManager
    }

    fun setTotal(ongkir: String) {
        tv_ongkir.text = Helper().gantiRupiah(ongkir)
        tv_total.text = Helper().gantiRupiah(Integer.valueOf(ongkir) + totalHarga)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        chekAlamat()
        super.onResume()
    }
}
