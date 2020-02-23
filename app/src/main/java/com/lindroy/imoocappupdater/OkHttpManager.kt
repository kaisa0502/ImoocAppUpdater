package com.lindroy.imoocappupdater

import android.os.Handler
import android.os.Looper
import com.lindroy.imoocappupdater.appupdater.net.INetCallback
import com.lindroy.imoocappupdater.appupdater.net.INetDownloadCallback
import com.lindroy.imoocappupdater.appupdater.net.INetManager
import okhttp3.*
import java.io.*
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

    override fun get(url: String, tag: Any?, callback: INetCallback) {
        // requestBuilder -> Request -> Call -> execute/enqueue
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
                callback.onSuccess(response.body()?.string() ?: "")
            }

        })
    }

    override fun download(url: String, targetFile: File, tag: Any?, callback: INetDownloadCallback) {
        if (targetFile.exists().not()) {
            //文件不存在则创建文件
            targetFile.parentFile.mkdirs()
        }
        //开始通过链接下载apk
        val builder = Request.Builder()
        val request = builder.url(url).get().tag(tag).build()
        val call = oKHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    callback.onFailed(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream: InputStream? = null
                var outStream: OutputStream? = null
                try {
                    val responseBody = response.body()!!
                    //文件总长度
                    val totalLen = responseBody.contentLength()
                    //当前保存的字节数
                    var curLen = 0
                    //从请求到的结果中获取输入流
                    inputStream = responseBody.byteStream()
                    outStream = FileOutputStream(targetFile)
                    //缓存数据的数组，用于将数据从输入流写入到输出流
                    val buffer = ByteArray(8 * 1024)  //大小为8k
                    var bufferLen = 0
                    while (inputStream!!.read(buffer).also { bufferLen = it } != -1) {
                        //写入输出流
                        outStream!!.write(buffer, 0, bufferLen)
                        outStream!!.flush()
                        curLen += bufferLen
                        //回调下载进度
                        handler.post {
                            callback.onProgress((curLen.toDouble() / totalLen * 100).toInt())
                        }
                    }
                    try {
                        //设置可操作性和可读写性，第二个参数表示是否只只归拥有者
                        targetFile.setExecutable(true, false) //可操作
                        targetFile.setReadable(true, false) //可读取
                        targetFile.setWritable(true, false) //可写入
                        handler.post {
                            callback.onSuccess(targetFile)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        handler.post {
                            callback.onFailed(e)
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    handler.post {
                        callback.onFailed(e)
                    }
                }finally {
                    inputStream?.close()
                    outStream?.close()
                }

            }

        })

    }

    override fun cancel(tag: Any) {
    }
}