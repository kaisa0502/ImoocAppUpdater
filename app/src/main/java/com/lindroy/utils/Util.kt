package com.lindroy.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import java.io.File

/**
 * @author Lin
 * @date 2020/2/23
 * @function
 */
/**
 * 获取应用版本号，默认为本应用
 * @return 失败时返回-1
 */
fun Context.getAppVersionCode(packageName: String = this.packageName): Int {
    return try {
        if (packageName.isBlank()) {
            -1
        } else {
            val pi = packageManager.getPackageInfo(packageName, 0)
            pi?.versionCode ?: -1
        }
    } catch (e: PackageManager.NameNotFoundException) {
        -1
    }
}

fun Activity.installApk(apkFile:File){

}