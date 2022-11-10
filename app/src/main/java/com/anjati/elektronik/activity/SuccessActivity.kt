package com.anjati.elektronik.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.anjati.elektronik.MainActivity
import com.anjati.elektronik.R
import com.anjati.elektronik.helper.Helper
import com.anjati.elektronik.model.Bank
import com.anjati.elektronik.model.Chekout
import com.anjati.elektronik.model.Transaksi
import com.anjati.elektronik.room.MyDatabase
import kotlinx.android.synthetic.main.activity_success.*
import kotlinx.android.synthetic.main.toolbar.*

class SuccessActivity : AppCompatActivity() {

    var nominal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        Helper().setToolbar(this, toolbar, "Bank Transfer")

        setValues()
        mainButton()
    }

    fun mainButton() {
        btn_copyNoRek.setOnClickListener {
            copyText(tv_nomorRekening.text.toString())
        }

        btn_copyNominal.setOnClickListener {
            copyText(nominal.toString())
        }

        btn_cekStatus.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
    }

    fun copyText(text: String) {
        val copyManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = ClipData.newPlainText("text", text)
        copyManager.setPrimaryClip(copyText)

        Toast.makeText(this, "Text berhasil di Kopi", Toast.LENGTH_LONG).show()
    }

    fun setValues() {
        val jsBank = intent.getStringExtra("bank")
        val jsTransaksi = intent.getStringExtra("transaksi")
        val jsCheckout = intent.getStringExtra("chekout")

        val bank = Gson().fromJson(jsBank, Bank::class.java)
        val transaksi = Gson().fromJson(jsTransaksi, Transaksi::class.java)
        val chekout = Gson().fromJson(jsCheckout, Chekout::class.java)

        // hapus keranjang
        val myDb = MyDatabase.getInstance(this)!!
        for (produk in chekout.products){
            myDb.daoKeranjang().deleteById(produk.id)
        }

        tv_nomorRekening.text = bank.nomor_rekening
        tv_namaPenerima.text = bank.card_name
//        image_bank.setImageResource(bank.image)
        Picasso.get()
                .load(bank.image)
                .into(image_bank)
        val htmlBMT = "<p><strong>Pembayaran BMT NU Ngasem melalui Mobile</strong></p>\n" +
                "<ol>\n" +
                "    <li>Buka Aplikasi BMT NU Ngasem mobile</li>\n" +
                "    <li>Masukkan Username dan Password Anda.</li>\n" +
                "    <li>Kemudian, Pilih <strong>Menu Transaksi</strong>.</li>\n" +
                "    <li>Pilih <strong>Transfer</strong> lalu <strong>Transfer In-House&nbsp;</strong>kemudian pilih Jenis Rekening yang akan Anda gunakan Contoh: &quot;00127 (Simpanan Syariah)&quot;.</li>\n" +
                "    <li>Pilih <strong>Menu Ketik Manual Rekening Tujuan</strong>. Masukkan <strong>Nominal Transfer</strong> sesuai yang tertera di atas, lalu Masukan <strong>Nomor Rekening Tujuan&nbsp;</strong>(Contoh: ${bank.nomor_rekening}).</li>\n" +
                "    <li>Isi Berita Transfer &quot;Bismillah bayar&quot;</li>\n" +
                "    <li>Lalu klik Tombol <strong>Kirim Permintaan Transfer</strong></li>\n" +
                "    <li><strong>Masukan Password&nbsp;</strong>kamu lalu klik tombol<strong>&nbsp;Transfer</strong></li>\n" +
                "    <li>Transaksi Anda telah selesai.</li>\n" +
                "</ol>\n" +
                "<p><br></p>"
        val htmlBNI = "<h4>Pembayaran BNI Virtual Account dengan ATM BNI</h4>\n" +
                "<ol>\n" +
                "    <li>Masukkan Kartu Anda.</li>\n" +
                "    <li>Pilih Bahasa.</li>\n" +
                "    <li>Masukkan PIN ATM Anda.</li>\n" +
                "    <li>Kemudian, pilih <strong>Menu Lainnya</strong>.</li>\n" +
                "    <li>Pilih <strong>Transfer</strong> dan pilih Jenis rekening yang akan Anda gunakan (Contoh: &quot;Dari Rekening Tabungan&quot;).</li>\n" +
                "    <li>Pilih <strong>Virtual Account Billing</strong>. Masukkan nomor Virtual Account Anda (Contoh: ${bank.nomor_rekening}).</li>\n" +
                "    <li>Tagihan yang harus dibayarkan akan muncul pada layar konfirmasi.</li>\n" +
                "    <li>Konfirmasi, apabila telah sesuai, lanjutkan transaksi.</li>\n" +
                "    <li>Transaksi Anda telah selesai.</li>\n" +
                "</ol>\n" +
                "<h4>Pembayaran BNI Virtual Account dengan Mobile Banking BNI</h4>\n" +
                "<ol>\n" +
                "    <li><span style=\"font-weight: 100;\">Akses BNI Mobile Banking melalui handphone.</span></li>\n" +
                "    <li><span style=\"font-weight: 100;\">Masukkan <em>User ID</em> dan <em>password.</em>&nbsp;</span></li>\n" +
                "    <li><span style=\"font-weight: 100;\">Pilih menu&nbsp;</span><strong>Transfer</strong>.</li>\n" +
                "    <li><span style=\"font-weight: 100;\">Pilih menu&nbsp;</span><strong>Virtual Account Billing</strong><span style=\"font-weight: 400;\">, lalu pilih rekening debet.</span></li>\n" +
                "    <li>Masukkan nomor Virtual Account Anda (Contoh: ${bank.nomor_rekening}) pada menu <strong>Input Baru</strong>.</li>\n" +
                "    <li>Tagihan yang harus dibayarkan akan muncul pada layar konfirmasi.</li>\n" +
                "    <li>Konfirmasi transaksi dan masukkan Password Transaksi.</li>\n" +
                "    <li>Pembayaran Anda Telah Berhasil.</li>\n" +
                "</ol>"

        if (bank.name == "BMT NU Ngasem"){
            webViewBNI.loadData(htmlBMT, "text/html", "UTF-8");
        } else if(bank.name == "BNI Syariah (VA)") {
            webViewBNI.loadData(htmlBNI, "text/html", "UTF-8");
        } else {
            webViewBNI.visibility = View.GONE
        }
        nominal = Integer.valueOf(transaksi.total_transfer) + transaksi.kode_unik
        tv_nominal.text = Helper().gantiRupiah(nominal)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }

}
