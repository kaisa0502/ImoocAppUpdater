package com.lindroy.imoocappupdater.appupdater.net

import java.io.File

/**
 * @author Lin
 * @date 2020/2/22
 * @function 网络请求管理类
 */
interface INetManager {
    fun get(url:String,tag:Any?=null,callback: INetCallback)

    fun download(url: String, targetFile: File,tag: Any? = null, callback: INetDownloadCallback)

    fun cancel(tag:Any)
}