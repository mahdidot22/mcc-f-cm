package com.mahdid.o.taha.pushnotification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.mahdid.o.taha.pushnotification.MainActivity.Companion.Notification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_token_add_update.*
import kotlinx.android.synthetic.main.activity_token_add_update.email
import kotlinx.android.synthetic.main.activity_token_add_update.psw
import org.json.JSONObject

class TokenAddUpdate : AppCompatActivity() {
    lateinit var FirebaseMessaging: FirebaseMessaging
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_add_update)
        FirebaseMessaging = Firebase.messaging
        btn_update.setOnClickListener {
            if (email.text.isNotEmpty() && psw.text.isNotEmpty()) {
                onTokenRefresh()
            } else {
                Toast.makeText(this, "Fill Fields", Toast.LENGTH_SHORT).show()
            }

        }


    }

    fun onTokenRefresh() {
        FirebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            }
            val token = task.result
            regData(token)
        }
    }

    fun regData(token: String?) {
        val user = JSONObject()
        user.put("email", email.text.toString())
        user.put("password", psw.text.toString())
        user.put("token", token)
        add_reg_token(user, token, "Registration operation", "operation goes successfully!!")
    }

    fun add_reg_token(data: JSONObject, token: String?, title: String, msg: String) {
        val reg_data = data
        val reg_URL = "https://mcc-users-api.herokuapp.com/add_reg_token"
        val regReq =
                JsonObjectRequest(Request.Method.PUT, reg_URL, reg_data,
                        {
                            Notification(reg_URL, msg, title, token)
                        }, {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                })
        MySingelton.getInstance()!!.addRequestQueue(regReq)
    }

}