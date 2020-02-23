package com.lindroy.imoocappupdater.appupdater.net

/**
 * @author Lin
 * @date 2020/2/22
 * @function 网络请求回调
 */
interface INetCallback {

    fun onSuccess(response: String)

    fun onFailed(throwable: Throwable)
}