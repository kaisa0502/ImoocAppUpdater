package com.lindroy.imoocappupdater

import android.os.Handler
import android.os.Looper
import com.lindroy.imoocappupdater.appupdater.net.INetCallback
import com.lindroy.imoocappupdater.appupdater.net.INetManager
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @author Lin
 * @date 2020/2/23
 * @function
 */
object OkHttpManager : INetManager {


    val instance by lazy {
        this
    }

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val oKHttpClient: OkHttpClient by lazy {
        with(OkHttpClient.Builder()) {
            connectTimeout(15L, TimeUnit.SECONDS)
            this
        }.build()
        //如果使用Https的话可能会发生握手的错误，需要调用builder.sslSocketFactory()传入证书
    }

    override fun get(url: String,  tag: Any?,callback: INetCallback) {
        val builder = Request.Builder()
        val request = builder.url(url).get().tag(tag).build()
        val call = oKHttpClient.newCall(request)
        //同步请求，在当前线程中执行
//        val response = call.execute()
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.body()?.string()?:"")
            }

        })
    }

    override fun download(url: String, targetFile: File, tag: Any, callback: INetCallback) {
    }

    override fun cancel(tag: Any) {
    }
}