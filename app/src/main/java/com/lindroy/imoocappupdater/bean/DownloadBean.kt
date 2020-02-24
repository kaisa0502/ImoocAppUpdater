package com.lindroy.imoocappupdater.bean

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

/**
 * @author Lin
 * @date 2020/2/23
 * @function
 */
data class DownloadBean(
    var content: String = "",
    var md5: String = "",
    var title: String = "",
    var url: String = "",
    var versionCode: String = ""
):Serializable {
    companion object {
        fun parse(response: String): DownloadBean? =
            try {
                val jsonObject = JSONObject(response)
                val title = jsonObject.optString("title")
                val content = jsonObject.optString("content")
                val url = jsonObject.optString("url")
                val md5 = jsonObject.optString("md5")
                val versionCode = jsonObject.optString("versionCode")
                DownloadBean(
                    title = title,
                    content = content,
                    url = url,
                    md5 = md5,
                    versionCode = versionCode
                )
            } catch (e: JSONException) {
                null
            }

    }
}