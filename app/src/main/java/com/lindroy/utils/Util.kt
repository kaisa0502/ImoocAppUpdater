package com.lindroy.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

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


fun File?.toMD5String(): String {
    if (this == null || this.isFile.not()) {
        return ""
    }
    val digest: MessageDigest
    var inputStream: FileInputStream? = null
    var len = 0
    val buffer = ByteArray(1024)

    try {
        digest = MessageDigest.getInstance("MD5")
        inputStream = FileInputStream(this)
        while (inputStream.read(buffer).also { len = it } != -1){
            digest.update(buffer,0,len)
        }
    }catch (e:Exception){
        e.printStackTrace()
        return ""
    }finally {
       try {
           inputStream?.close()
       }catch (e:IOException){
           e.printStackTrace()
       }
    }
    val result = digest.digest()
    return BigInteger(1,result).toString(16)


}