package com.anjati.elektronik.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.anjati.elektronik.R
import com.anjati.elektronik.adapter.AdapterBank
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.helper.Helper
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.anjati.elektronik.model.*
import kotlinx.android.synthetic.main.activity_pembayaran.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        Helper().setToolbar(this, toolbar, "Pembayaran")

        getBank()
    }

    private fun getBank() {
        ApiConfig.instanceRetrofit.getBank().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.bank.isNotEmpty()) {
                    listBank = res.bank
                    displayBank()
                }
            }
        })
    }

    private var listBank: ArrayList<Bank> = ArrayList()

    fun displayBank() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_data.layoutManager = layoutManager
        rv_data.adapter = AdapterBank(listBank, object : AdapterBank.Listeners {
            override fun onClicked(data: Bank, index: Int) {
                bayar(data)
            }
        })
    }

    fun bayar(bank: Bank) {
        val json = intent.getStringExtra("extra")!!.toString()
        val chekout = Gson().fromJson(json, Chekout::class.java)
        chekout.bank = bank.name

        val loading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        loading.setTitleText("Loading....").setContentText("Pembelian sedang di Proses").show()
        //Log.d("Respon:", "json:" + chekout)//

        ApiConfig.instanceRetrofit.chekout(chekout).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                loading.dismiss()
                error(t.message.toString())
               // Toast.makeText(this, "Error:" + t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                loading.dismiss()
                if (!response.isSuccessful) {
                    error(response.message())
                    return
                }

                val respon = response.body()!!
                Log.d("Respon:", ""+ response.body())
                if (respon.success == 1) {
                    success("Invoice Pembayaran telah di buat, silahkan Transfer Pembayaran ya!")
                    Handler(getMainLooper()).postDelayed({
                        val jsBank = Gson().toJson(bank, Bank::class.java)
                        val jsTransaksi = Gson().toJson(respon.transaksi, Transaksi::class.java)
                        val jsChekout = Gson().toJson(chekout, Chekout::class.java)

                        val intent = Intent(this@PembayaranActivity, SuccessActivity::class.java)
                        intent.putExtra("bank", jsBank)
                        intent.putExtra("transaksi", jsTransaksi)
                        intent.putExtra("chekout", jsChekout)
                        startActivity(intent)
                    }, 5000)
                } else {
                    error(respon.message)
                    Toast.makeText(this@PembayaranActivity, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun error(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Maaf Ada Masalah...")
                .setContentText(pesan)
                .show()
    }

    fun success(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Berhasil...")
                .setContentText(pesan)
                .show()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
