package com.mahdid.o.taha.pushnotification

import android.app.Notification
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.OutputStream
import java.lang.System.setProperty
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.Security.setProperty

class MainActivity : AppCompatActivity() {
    lateinit var FirebaseMessaging: FirebaseMessaging
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseMessaging = Firebase.messaging
        val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        btn_login.setOnClickListener {
            if (email.text.isNotEmpty() && psw.text.isNotEmpty()) {
                onTokenRefresh("login")
            } else {
                Toast.makeText(this, "Fill Fields", Toast.LENGTH_SHORT).show()
            }
            btnStyle(params, "login")

        }

        btn_newUser.setOnClickListener {
            if (fname.text.isNotEmpty() && lname.text.isNotEmpty() && email.text.isNotEmpty() && psw.text.isNotEmpty()) {
                onTokenRefresh("newUser")
            } else {
                Toast.makeText(this, "Fill Fields", Toast.LENGTH_SHORT).show()
            }
            btnStyle(params, "newUser")

        }

    }


    fun onTokenRefresh(channel: String) {
        FirebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            }
            val token = task.result
            when (channel) {
                "newUser" -> {
                    newUserData(token)
                }
                "login" -> {
                    loginData(token)
                }
            }
        }
    }

    fun newUserData(token: String?) {
        val user = JSONObject()
        user.put("firstName", fname.text.toString())
        user.put("secondName", lname.text.toString())
        user.put("email", email.text.toString())
        user.put("password", psw.text.toString())
        user.put("token", token)
        addNewUser(user, token, "Adding operation", "user added successfully!!")
    }

    fun addNewUser(data: JSONObject, token: String?, title: String, msg: String) {
        val newUserData = data
        val addUser_URL = "https://mcc-users-api.herokuapp.com/add_new_user"
        val newUserReq =
                JsonObjectRequest(Request.Method.POST, addUser_URL, newUserData,
                        {
                            Notification(addUser_URL, msg, title, token)
                        }, {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                })
        MySingelton.getInstance()!!.addRequestQueue(newUserReq)
    }

    fun loginData(token: String?) {
        val user = JSONObject()
        user.put("email", email.text.toString())
        user.put("password", psw.text.toString())
        login(user, token, "Login operation", "user login successfully!!")
    }

    fun login(data: JSONObject, token: String?, title: String, msg: String) {
        val userLogin = data
        val login_URL = "https://mcc-users-api.herokuapp.com/login"
        val loginReq =
                JsonObjectRequest(Request.Method.POST, login_URL, userLogin,
                        {
                            Notification(login_URL, msg, title, token)
                        }, {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                })
        MySingelton.getInstance()!!.addRequestQueue(loginReq)
    }


    fun btnStyle(params: LinearLayout.LayoutParams, checker: String) {

        when (checker) {
            "login" -> {
                fname.visibility = EditText.GONE
                lname.visibility = EditText.GONE
                params.setMargins(5, 5, 5, 5)
                btn_login.layoutParams = params
                btn_login.setTextColor(Color.GREEN)
                btn_newUser.setTextColor(Color.WHITE)
            }
            "newUser" -> {
                fname.visibility = EditText.VISIBLE
                lname.visibility = EditText.VISIBLE
                params.setMargins(-7, -7, 0, -7)
                btn_login.layoutParams = params
                btn_login.setTextColor(Color.WHITE)
                btn_newUser.setTextColor(Color.GREEN)
            }
        }

    }


    companion object {
        fun Notification(endpoint: String, msg: String, title: String, token: String?) {
            val url = URL(endpoint)
            val http: HttpURLConnection = url.openConnection() as HttpURLConnection
            http.requestMethod = "POST"
            http.doOutput = true
            http.setRequestProperty("Accept", "application/json")
            http.setRequestProperty("Authorization", "Bearer AAAAxALqYN4:APA91bEnwHAm6e3miuXcvqWTew5bGDx7381ablZIFjizgGLG8qiQi0Sse_Z3-EV_J83vxZ_XTuRgJPnGuAuxqhhCCXxqGAQoDd-0LkNxxxLqOnwaI1UBV5jsOnhFfISnaj2wm-DgtpRQ")
            http.setRequestProperty("Content-Type", "application/json")

            val d = "{\n  \"to\":\"${token}\",\n  \"notification\":{\n    \"title\":\"${title}\",\n    \"body\":\"${msg}\"\n  },\n  \"time to be live\":600\n}"
            val out: ByteArray = d.toByteArray(StandardCharsets.UTF_8)
            val stream: OutputStream = http.outputStream
            stream.write(out)
            println(http.responseCode.toString() + " " + http.responseMessage)
            http.disconnect()
        }

    }

}