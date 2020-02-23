package com.lindroy.imoocappupdater

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lindroy.imoocappupdater.appupdater.AppUpdater
import com.lindroy.imoocappupdater.appupdater.net.INetCallback
import com.lindroy.imoocappupdater.appupdater.net.INetDownloadCallback
import com.lindroy.imoocappupdater.bean.DownloadBean
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val DOWNLOAD_URL = "http://59.110.162.30/app_updater_version.json"
private const val TAG = "Tag"

class MainActivity : AppCompatActivity() {
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        AppUpdater.setNetManager(OkHttpManager.instance)
        btnUpdate.setOnClickListener {
            AppUpdater.getNetManager().get(DOWNLOAD_URL, callback = object : INetCallback {
                override fun onSuccess(response: String) {
                    val bean = DownloadBean.parse(response)
                    if (bean != null) {
                        val file = File(cacheDir, "target.apk")
                        AppUpdater.getNetManager().download(
                            bean.url,
                            targetFile = file,
                            callback = object : INetDownloadCallback {
                                override fun onSuccess(apkFile: File) {
                                    //下载成功后路径：/data/user/0/com.lindroy.imoocappupdater/cache/target.apk，
                                    // 因为是在内存中，所以在手机的文件管理中无法查看
                                    Log.d(TAG, "下载成功:${apkFile.absolutePath}")
                                }

                                override fun onProgress(progress: Int) {
                                    Log.d(TAG, "progress=$progress")
                                }

                                override fun onFailed(throwable: Throwable) {
                                    Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show()
                                }
                            })
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    Log.d("Tag", "onFailed")
                }
            })
        }
    }
}
