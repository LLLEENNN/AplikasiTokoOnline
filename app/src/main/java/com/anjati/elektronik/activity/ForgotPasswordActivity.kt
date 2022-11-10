package com.anjati.elektronik.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.anjati.elektronik.R
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.helper.SharedPref
import com.anjati.elektronik.model.ResponModel
import com.anjati.elektronik.model.User
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.edt_email
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        s = SharedPref(this)

        btn_reset.setOnClickListener {
            checkEmail()
        }

    }

    private fun checkEmail() {
        if (edt_email.text.isEmpty()) {
            edt_email.error = "Kolom Email tidak boleh kosong"
            edt_email.requestFocus()
            return
        }

        pbForget.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.checkEmail(edt_email.text.toString()).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                pbForget.visibility = View.GONE
                Toast.makeText(this@ForgotPasswordActivity, "Error:" + t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                pbForget.visibility = View.GONE
                val respon = response.body()!!
                if (respon.success == 1) {
                    Toast.makeText(this@ForgotPasswordActivity, "Hey " + respon.user.name + "Yuk, Reset Password kamu!", Toast.LENGTH_SHORT).show()
                    val user = User()
                    user.email = respon.user.email
                    val json = Gson().toJson(user, User::class.java)
                    val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("check", json)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}