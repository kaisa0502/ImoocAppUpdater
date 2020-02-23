package com.lindroy.imoocappupdater.appupdater.net

import java.io.File

/**
 * @author Lin
 * @date 2020/2/23
 * @function 下载回调
 */
interface INetDownloadCallback {
    /**
     * 下载成功
     * @param apkFile:安装文件
     */
    fun onSuccess(apkFile: File)

    /**
     * 下载中
     * @param progress:下载进度
     */
    fun onProgress(progress:Int)

    /**
     * 下载失败
     */
    fun onFailed(throwable: Throwable)
}