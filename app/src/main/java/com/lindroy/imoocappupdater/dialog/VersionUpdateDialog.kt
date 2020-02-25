package com.lindroy.imoocappupdater.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.lindroy.imoocappupdater.R
import com.lindroy.imoocappupdater.appupdater.AppUpdater
import com.lindroy.imoocappupdater.appupdater.net.INetDownloadCallback
import com.lindroy.imoocappupdater.bean.DownloadBean
import com.lindroy.utils.installApk
import com.lindroy.utils.toMD5String
import kotlinx.android.synthetic.main.dialog_version_update.*
import java.io.File


/**
 * @author Lin
 * @date 2020/2/23
 * @function 版本更新对话框
 */
private const val KEY_DOWNLOAD = "key_download"
private const val TAG = "VersionUpdate"

class VersionUpdateDialog : DialogFragment() {
    private lateinit var mContext: Context

    private val downloadBean by lazy {
        arguments?.getSerializable(KEY_DOWNLOAD) as DownloadBean
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        fun show(fm: FragmentManager, bean: DownloadBean) {
            VersionUpdateDialog().apply {
                arguments = Bundle().apply { putSerializable(KEY_DOWNLOAD, bean) }
            }.show(fm, "")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_version_update, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initView()
    }

    override fun onStart() {
        super.onStart()
        //设置对话框的宽高
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window!!.setLayout((dm.widthPixels * 0.8).toInt(), dialog.window!!.attributes.height)
    }

    private fun initView() {
        tvMessage.text = downloadBean.content
        btnUpdate.setOnClickListener { v ->
            val file = File(mContext.cacheDir, "target.apk")
            //使用MD5判断安装包是否已经存在
            val fileMd5 = file.toMD5String()
            Log.d(TAG,"MD5=$fileMd5")
            if(fileMd5 == downloadBean.md5){
                Log.d(TAG,"文件已存在，可直接安装")
                mContext.installApk(file)
                dismiss()
                return@setOnClickListener
            }
            AppUpdater.getNetManager().download(
                downloadBean.url,
                targetFile = file,
                tag = this,
                callback = object : INetDownloadCallback {
                    override fun onSuccess(apkFile: File) {
                        //下载成功后路径：/data/user/0/com.lindroy.imoocappupdater/cache/target.apk，
                        // 因为是在内存中，所以在手机的文件管理中无法查看
                        Log.d(TAG, "下载成功:${apkFile.absolutePath}")
                        mContext.installApk(apkFile)
                        dismiss()
                    }

                    override fun onProgress(progress: Int) {
                        v.isEnabled = false
                        btnUpdate.text = "$progress%"
                        Log.d(TAG, "progress=$progress")
                    }

                    override fun onFailed(throwable: Throwable) {
                        v.isEnabled = false
                        Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

     override fun onDismiss(dialog: DialogInterface?) {
         super.onDismiss(dialog)
         //对话框关闭的时候停止请求，否则对话框销毁后btnUpdate为null，从而报空指针
         AppUpdater.getNetManager().cancel(this)
     }
}