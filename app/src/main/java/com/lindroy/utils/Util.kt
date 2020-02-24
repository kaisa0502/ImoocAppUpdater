package com.lindroy.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
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

fun Context.installApk(apkFile: File) {
    startActivity(Intent().apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        action = Intent.ACTION_VIEW
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //权限跟清单文件中的 android:authorities 一致
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            FileProvider.getUriForFile(
                this@installApk,
                "${this@installApk.packageName}.fileprovider",
                apkFile
            )
        } else Uri.fromFile(apkFile)
        setDataAndType(uri, "application/vnd.android.package-archive")
    })
}