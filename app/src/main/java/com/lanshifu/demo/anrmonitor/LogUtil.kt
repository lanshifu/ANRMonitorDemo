package com.lanshifu.demo.anrmonitor

import android.util.Log
import java.lang.StringBuilder

/**
 * @author lanxiaobin
 * @date 2021/4/21.
 */
object LogUtil {

    val TAG = "PerformanceManager"

    private enum class LEVEL {
        V, D, I, W, E
    }

    var showStackTrace = false


    fun logv(tag: String = TAG, message: Any) = log(LEVEL.V, tag, message.toString())
    fun logd(message: Any) = logd(TAG, message.toString())
    fun logi(message: Any) = logi(TAG, message.toString())
    fun logw(message: Any) = logw(TAG, message.toString())
    fun loge(message: Any) = loge(TAG, message.toString())
    fun logd(tag: String = TAG, message: Any) = log(LEVEL.D, tag, message.toString())
    fun logi(tag: String = TAG, message: Any) = log(LEVEL.I, tag, message.toString())
    fun logw(tag: String = TAG, message: Any) = log(LEVEL.W, tag, message.toString())
    fun loge(tag: String = TAG, message: Any) = log(LEVEL.E, tag, message.toString())

    private fun log(level: LEVEL, tag: String, message: String) {

        val tagBuilder = StringBuilder()
        tagBuilder.append(tag)

        if (showStackTrace) {
            val stackTrace = Thread.currentThread().stackTrace[5]
            tagBuilder.append(" ${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})")
        }
        when (level) {
            LEVEL.V -> Log.v(tagBuilder.toString(), message)
            LEVEL.D -> Log.d(tagBuilder.toString(), message)
            LEVEL.I -> Log.i(tagBuilder.toString(), message)
            LEVEL.W -> Log.w(tagBuilder.toString(), message)
            LEVEL.E -> Log.e(tagBuilder.toString(), message)
        }
    }
}