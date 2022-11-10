package com.anjati.elektronik.fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog

import com.anjati.elektronik.R
import com.anjati.elektronik.activity.MasukActivity
import com.anjati.elektronik.activity.RiwayatActivity
import com.anjati.elektronik.helper.SharedPref
import com.squareup.picasso.Picasso
import com.anjati.elektronik.activity.FavoriteActivity
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.model.ResponModel
import com.anjati.elektronik.room.MyDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class AkunFragment : Fragment() {

    lateinit var s: SharedPref
    lateinit var btnLogout: TextView
    lateinit var tvNama: TextView
    lateinit var tvEmail: TextView
    lateinit var tvPhone: TextView
    lateinit var myDb: MyDatabase

    lateinit var avatar: ImageView


    lateinit var btnRiwayat: RelativeLayout
    lateinit var btnFavorite: RelativeLayout
    lateinit var btnHelp: RelativeLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_akun, container, false)
        init(view)

        myDb = MyDatabase.getInstance(requireActivity())!! // call database
        s = SharedPref(requireActivity())

        mainButton()
        setData()
        return view
    }

    fun mainButton() {
        btnLogout.setOnClickListener {
            s.setStatusLogin(false)
            myDb.daoAlamat().deleteAll()
            val intent = Intent(requireActivity(), MasukActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btnFavorite.setOnClickListener {
            startActivity(Intent(requireActivity(), FavoriteActivity::class.java))
        }

        btnHelp.setOnClickListener {
            val loading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
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
                        val message = "Bismillahirrohmanirrohim..."
                        try {
//                            val sendIntent = Intent().apply {
//
////                                action = Intent.ACTION_SEND
////                                putExtra(Intent.EXTRA_TEXT, message)
////                                putExtra("jid", "${a.nomor_wa}@s.whatsapp.net")
////                                type = "text/plain"
////                                setPackage("com.whatsapp")
//                            }
                            val webIntent: Intent = Uri.parse("https://api.whatsapp.com/send?phone="+a.nomor_wa+"&text=Assalamualaikum%20Kak").let { webpage ->
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
                        Toast.makeText(context, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }



        btnRiwayat.setOnClickListener {
            startActivity(Intent(requireActivity(), RiwayatActivity::class.java))
        }
    }

    fun setData() {

        if (s.getUser() == null) {
//            val intent = Intent(activity, LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
            return
        }

        val user = s.getUser()!!
        Picasso.get()
                .load(user.avatar)
                .placeholder(R.drawable.product)
                .error(R.drawable.product)
                .into(avatar)

        tvNama.text = user.name
        tvEmail.text = user.email
        tvPhone.text = user.phone
    }

    private fun init(view: View) {
        btnLogout = view.findViewById(R.id.btn_logout)
        tvNama = view.findViewById(R.id.tv_nama)
        tvEmail = view.findViewById(R.id.tv_email)
        avatar = view.findViewById(R.id.img_avatar)
        tvPhone = view.findViewById(R.id.tv_phone)
        btnRiwayat = view.findViewById(R.id.btn_riwayat)
        btnFavorite = view.findViewById(R.id.btn_favorite_ya)
        btnHelp = view.findViewById(R.id.btn_help)

    }


}
