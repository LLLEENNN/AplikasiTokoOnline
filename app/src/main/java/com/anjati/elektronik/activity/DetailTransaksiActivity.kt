package com.anjati.elektronik.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.anjati.elektronik.R
import com.anjati.elektronik.adapter.AdapterProdukTransaksi
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.model.*
import kotlinx.android.synthetic.main.activity_detail_transaksi.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.util.*


class DetailTransaksiActivity : AppCompatActivity() {

    var transaksi = Transaksi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)
        Helper().setToolbar(this, toolbar, "Detail Transaksi")

        val json = intent.getStringExtra("transaksi")
        transaksi = Gson().fromJson(json, Transaksi::class.java)

        setData(transaksi)
        displayProduk(transaksi.details)
        mainButton()
    }

    private fun mainButton() {

        btn_confirm_pengiriman.setOnClickListener(View.OnClickListener {

            val loading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            loading.setTitleText("Bismillahirrohmanirrohim....").setContentText("Sedang mengalihkan ke Proses Konfirmasi").show()

            ApiConfig.instanceRetrofit.getService().enqueue(object : Callback<ResponModel> {
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
                        var a = respon.service[0]
                        val message = "Bismillahirrohmanirrohim... \n\nAssalamualaikum kak saya mau menanyakan " +
                                "Orderan dengan Rincian sebagai berikut :" +
                                "\n\n*Nama* = ${transaksi.name}  " +
                                "\n\n*Kode Invoice* = ${transaksi.kode_trx}  " +
                                "\n\nTerima Kasih Atas waktunya kak.. " +
                                "\n\nMohon untuk segera di kirim ya!"
                        val pesan = "Bismillahirrohmanirrohim...+%0D%0A%0D%0AAssalamualaikum+kak+" +
                                "saya+mau+Konfirmasi+Pembayaran+dengan+Rincian+sebagai+berikut+%3A%0D%0A%0D%0A" +
                                "Nama+%3D+${URLEncoder.encode(transaksi.name)}++%0D%0A" +
                                "Kode+Invoice+%3D+${URLEncoder.encode(transaksi.kode_trx)}%0A" +
                                "Total+Transfer+%3D+${Helper().gantiRupiah(Integer.valueOf(transaksi.total_transfer) + Integer.valueOf(transaksi.kode_unik))}+%0D%0A" +
                                "Metode+Pembayaran+%3D+Transfer+BNI+Syariah+%28VA%29+%0D%0A%0D%0A" +
                                "Terima+Kasih+Atas+waktunya+kak..+" +
                                "%0D%0A%0D%0ABerikut+Bukti+Transfer+di+bawah+ini%21%0D%0ASilahkan+Upload%2FKirim+Bukti+Transfer+Pembayaran%21"
                        try {
                            val webIntent: Intent = Uri.parse("https://api.whatsapp.com/send?phone="+a.nomor_wa+"&text=Assalamualaikum%20Kak").let { webpage ->
                                Intent(Intent.ACTION_VIEW, webpage)
                            }
                            startActivity(webIntent)
//                            val sendIntent = Intent().apply {
//                                action = Intent.ACTION_SEND
//                                putExtra(Intent.EXTRA_TEXT, message)
//                                putExtra("jid", "${a.nomor_wa}@s.whatsapp.net")
//                                type = "text/plain"
//                                setPackage("com.whatsapp")
//                            }
//                            startActivity(sendIntent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            val appPackageName = "com.whatsapp"
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                            } catch (e: android.content.ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                            }
                        }

                    } else {
                        error(respon.message)
                        Toast.makeText(this@DetailTransaksiActivity, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        })


        btn_batal.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Apakah anda yakin?")
                    .setContentText("Transaksi akan di batalkan dan tidak bisa di kembalikan!")
                    .setConfirmText("Yes, Batalkan")
                    .setConfirmClickListener {
                        it.dismissWithAnimation()
                        batalTransaksi()
                    }
                    .setCancelText("Tutup")
                    .setCancelClickListener {
                        it.dismissWithAnimation()
                    }.show()
        }

        btn_confirm.setOnClickListener {

            val loading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            loading.setTitleText("Loading....").setContentText("Sedang mengalihkan ke Proses Konfirmasi").show()

            ApiConfig.instanceRetrofit.getService().enqueue(object : Callback<ResponModel> {
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
                        var a = respon.service[0]
                        val message = "Bismillahirrohmanirrohim... \n\nAssalamualaikum kak saya mau Konfirmasi " +
                                "Pembayaran dengan Rincian sebagai berikut :" +
                                "\n\n*Nama* = ${transaksi.name}  " +
                                "\n\n*Kode Invoice* = ${transaksi.kode_trx} \n" +
                                "*Total Transfer* = ${Helper().gantiRupiah(Integer.valueOf(transaksi.total_transfer) + Integer.valueOf(transaksi.kode_unik))} " +
                                "\n*Metode Pembayaran* = ${transaksi.bank} \n\nTerima Kasih Atas waktunya kak.. \n\nBerikut Bukti Transfer di bawah ini!" +
                                "\n*Silahkan Upload/Kirim Bukti Transfer Pembayaran!*"

                        val pesan = "Bismillahirrohmanirrohim...+%0D%0A%0D%0AAssalamualaikum+kak+" +
                                "saya+mau+Konfirmasi+Pembayaran+dengan+Rincian+sebagai+berikut+%3A%0D%0A%0D%0A" +
                                "Nama+%3D+${URLEncoder.encode(transaksi.name)}++%0D%0A" +
                                "Kode+Invoice+%3D+${URLEncoder.encode(transaksi.kode_trx)}%0A" +
                                "Total+Transfer+%3D+${Helper().gantiRupiah(Integer.valueOf(transaksi.total_transfer) + Integer.valueOf(transaksi.kode_unik))}+%0D%0A" +
                                "Metode+Pembayaran+%3D+Transfer+${URLEncoder.encode(transaksi.bank)}+%0D%0A%0D%0A" +
                                "Terima+Kasih+Atas+waktunya+kak..+" +
                                "%0D%0A%0D%0ABerikut+Bukti+Transfer+di+bawah+ini%21%0D%0ASilahkan+Upload%2FKirim+Bukti+Transfer+Pembayaran%21"
                        try {
//                            val sendIntent = Intent().apply {
//                                action = Intent.ACTION_SEND
//                                putExtra(Intent.EXTRA_TEXT, message)
//                                putExtra("jid", "${a.nomor_wa}@s.whatsapp.net")
//                                type = "text/plain"
//                                setPackage("com.whatsapp")
//                            }
                            val webIntent: Intent = Uri.parse("https://api.whatsapp.com/send?phone="+a.nomor_wa+"&text="+ pesan).let { webpage ->
                                Intent(Intent.ACTION_VIEW, webpage)
                            }
                            startActivity(webIntent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            val appPackageName = "com.whatsapp"
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                            } catch (e: android.content.ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                            }
                        }

                    } else {
                        error(respon.message)
                        Toast.makeText(this@DetailTransaksiActivity, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })


        }

    }

    fun batalTransaksi() {
        val loading = SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.PROGRESS_TYPE)
        loading.setTitleText("Loading...").show()
        ApiConfig.instanceRetrofit.batalChekout(transaksi.id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                loading.dismiss()
                error(t.message.toString())
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                loading.dismiss()
                val res = response.body()!!
                if (res.success == 1) {
                    SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success...")
                            .setContentText("Transaksi berhasil dibatalkan")
                            .setConfirmClickListener {
                                it.dismissWithAnimation()
                                onBackPressed()
                            }
                            .show()

//                    Toast.makeText(this@DetailTransaksiActivity, "Transaksi berhasil di batalkan", Toast.LENGTH_SHORT).show()
//                    onBackPressed()
//                    displayRiwayat(res.transaksis)
                } else {
                    error(res.message)
                }
            }
        })
    }

    fun error(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .show()
    }


    fun setData(t: Transaksi) {
        tv_status.text = t.status

//      Set data tanggal dan waktu
        tv_time.text = Helper().timeFormat(t.created_at)
        tv_tgl.text = Helper().dateFormat(t.created_at)

        if (t.bank == "COD") {
            method.text = "Cash On Delivery"
        } else {
            method.text = "Transfer Bank " + t.bank
        }
        tv_penerima.text = t.name + " - " + t.phone
        tv_alamat.text = t.detail_lokasi
        tv_kodeUnik.text = Helper().gantiRupiah(t.kode_unik)
        tv_totalBelanja.text = Helper().gantiRupiah(t.total_harga)

        val totalPrice = t.kode_unik.toInt() + t.total_transfer.toInt()
        tv_ongkir.text = Helper().gantiRupiah(t.ongkir)
        tv_total.text = Helper().gantiRupiah(totalPrice)

        if (t.status != "Menunggu Pembayaran") div_footer.visibility = View.GONE
        if (t.status != "Menunggu Pembayaran") div_confirm.visibility = View.VISIBLE

        var color = getColor(R.color.menungu)
        if (t.status == "SELESAI") color = getColor(R.color.selesai)
        else if (t.status == "BATAL") color = getColor(R.color.batal)

        tv_status.setTextColor(color)
    }

    fun displayProduk(transaksis: ArrayList<DetailTransaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_produk.adapter = AdapterProdukTransaksi(transaksis)
        rv_produk.layoutManager = layoutManager
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}
