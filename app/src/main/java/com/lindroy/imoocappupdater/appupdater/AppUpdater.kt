package com.lindroy.imoocappupdater.appupdater

import com.lindroy.imoocappupdater.appupdater.net.INetManager

/**
 * @author Lin
 * @date 2020/2/23
 * @function
 */
object AppUpdater {

    private lateinit var netManager: INetManager

    fun getNetManager() = netManager

    /**
     * 采用接口隔离原则，由传递参数的使用者决定使用哪一种网络框架
     */
    fun setNetManager(netManager: INetManager) {
        this.netManager = netManager
    }

}