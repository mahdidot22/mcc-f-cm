package com.mahdid.o.taha.pushnotification

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MySingelton:Application() {
    val TAG = "mdot"
    private var mRequestQueue:RequestQueue? = null

    companion object{
        private var mInstance:MySingelton? = null

        fun getInstance():MySingelton?{
            return mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    private fun getRequestQueue():RequestQueue?{
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(this)
        }
        return mRequestQueue
    }


    fun <T> addRequestQueue(req: Request<T>,tag:String?){
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue()!!.add(req)
    }

    fun <T> addRequestQueue(req: Request<T>){
        req.tag = TAG
        getRequestQueue()!!.add(req)
    }

    fun cancelPendingRequest(tag:Any?){
        if (mRequestQueue!= null){
            mRequestQueue!!.cancelAll(tag)
        }
    }


}
