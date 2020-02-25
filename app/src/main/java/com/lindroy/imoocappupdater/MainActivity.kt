package com.lindroy.imoocappupdater

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lindroy.imoocappupdater.appupdater.AppUpdater
import com.lindroy.imoocappupdater.appupdater.net.INetCallback
import com.lindroy.imoocappupdater.bean.DownloadBean
import com.lindroy.imoocappupdater.dialog.VersionUpdateDialog
import com.lindroy.utils.getAppVersionCode
import kotlinx.android.synthetic.main.activity_main.*

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
            AppUpdater.getNetManager()
                .get(DOWNLOAD_URL, tag = this, callback = object : INetCallback {
                    override fun onSuccess(response: String) {
                        val bean = DownloadBean.parse(response)
                        if (bean == null) {
                            Toast.makeText(context, "版本更新接口返回数据异常", Toast.LENGTH_LONG).show()
                            return
                        }
                        //检测是否需要更新
                        try {
                            val versionCode = bean.versionCode.toLong()
                            if (versionCode <= getAppVersionCode()) {
                                Toast.makeText(context, "已经是最新版本，无需更新", Toast.LENGTH_SHORT).show()
                                return
                            }
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                            Toast.makeText(context, "版本检测接口返回版本号异常", Toast.LENGTH_SHORT).show()
                            return
                        }
                        VersionUpdateDialog.show(supportFragmentManager, bean)

                    }

                    override fun onFailed(throwable: Throwable) {
                        Log.d("Tag", "onFailed")
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppUpdater.getNetManager().cancel(this)
    }
}
