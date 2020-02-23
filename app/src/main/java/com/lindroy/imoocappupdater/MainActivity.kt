package com.lindroy.imoocappupdater

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lindroy.imoocappupdater.appupdater.AppUpdater
import com.lindroy.imoocappupdater.appupdater.net.INetCallback
import kotlinx.android.synthetic.main.activity_main.*

private const val DOWNLOAD_URL = "http://59.110.162.30/app_updater_version.json"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppUpdater.setNetManager(OkHttpManager.instance)
        btnUpdate.setOnClickListener {
            AppUpdater.getNetManager().get(DOWNLOAD_URL,callback = object :INetCallback{
                override fun onSuccess(response: String) {
                    Log.d("Tag","onSuccess:"+response)
                }

                override fun onFailed(throwable: Throwable) {
                    Log.d("Tag","onFailed")

                }

            })
        }
    }
}
