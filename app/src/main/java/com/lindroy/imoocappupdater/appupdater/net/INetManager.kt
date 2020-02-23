package com.lindroy.imoocappupdater.appupdater.net

import java.io.File

/**
 * @author Lin
 * @date 2020/2/22
 * @function 网络请求管理类
 */
interface INetManager {
    fun get(url:String,callback: INetCallback,tag:Any)

    fun download(url: String, targetFile: File, callback: INetCallback,tag: Any)

    fun cancel(tag:Any)
}