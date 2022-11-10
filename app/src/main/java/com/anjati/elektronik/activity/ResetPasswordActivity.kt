package com.anjati.elektronik.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.anjati.elektronik.R
import com.anjati.elektronik.app.ApiConfig
import com.anjati.elektronik.model.ResponModel
import com.anjati.elektronik.model.User
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.edt_password
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        btnResetPassword.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val json = intent.getStringExtra("check")
        val user = Gson().fromJson(json, User::class.java)

        val email = user.email

        if (edt_password.text.isEmpty()) {
            edt_password.error = "Password tidak boleh kosong!"
            edt_password.requestFocus()
            return
        } else if (edt_password2.text.isEmpty()){
            edt_password2.error = "Password tidak boleh kosong!"
            edt_password2.requestFocus()
            return
        }

        if (edt_password.text.toString() == edt_password2.text.toString()){

            pbChange.visibility = View.VISIBLE
            ApiConfig.instanceRetrofit.resetPassword(email, edt_password.text.toString()).enqueue(object : Callback<ResponModel> {
                override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                    pbChange.visibility = View.GONE
                    Toast.makeText(this@ResetPasswordActivity, "Error:" + t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                    pbChange.visibility = View.GONE
                    val respon = response.body()!!
                    if (respon.success == 1) {
                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ResetPasswordActivity, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        } else {
            Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show()
        }



    }
}