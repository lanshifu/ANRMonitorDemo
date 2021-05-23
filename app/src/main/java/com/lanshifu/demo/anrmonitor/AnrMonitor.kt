package com.lanshifu.demo.anrmonitor

import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.lanshifu.demo.anrmonitor.LogUtil.logd
import com.lanshifu.demo.anrmonitor.LogUtil.loge
import com.lanshifu.demo.anrmonitor.LogUtil.logi
import com.lanshifu.demo.anrmonitor.LogUtil.logw

/**
 * @author lanxiaobin
 * @date 5/15/21
 *
 *
 */
class AnrMonitor(lifecycle: Lifecycle) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        logd("onDestroy")
        stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        logd("onPause")
        pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logd("onResume")
        start()
    }

    private var mAnrMonitorThread: HandlerThread? = null
    private var mAnrMonitorHandler: Handler? = null

    private val mMainHandler by lazy { Handler(Looper.getMainLooper()) }

    private val THREAD_CHCEK_INTERVAL = 1000L

    //主线程卡顿5s就算ANR
    private val ARN_TIMEOUT_SECOND = 5


    @Volatile
    var mHadReport = false //不重复上报

    @Volatile
    var blockTime = 0

    @Volatile
    var isPause = false

    fun start() {

        isPause = false
        if (mAnrMonitorThread == null) {
            mAnrMonitorThread = object : HandlerThread("AnrMonitor") {
                override fun onLooperPrepared() {
                    mAnrMonitorHandler = Handler(mAnrMonitorThread!!.looper)
                    resetFlagAndsendMainMessage()
                    sendDelayThreadMessage()
                }
            }
            mAnrMonitorThread?.start()
        } else {
            resetFlagAndsendMainMessage()
            sendDelayThreadMessage()
        }

    }

    private val mThreadRunnable = Runnable {
        //每隔1s检测一下
        blockTime++
        if (!mainHandlerRunEnd && !isDebugger()) {
            logw(TAG, "mThreadRunnable: main thread may be block at least $blockTime s")
        }

        //主线程的标志位5s还没更新，说明主线程卡顿了
        if (blockTime >= ARN_TIMEOUT_SECOND) {
            if (!mainHandlerRunEnd && !isDebugger() && !mHadReport) {
                mHadReport = true
                //5s了，主线程还没更新这个标志，ANR
                loge(TAG, "ANR->main thread may be block at least $blockTime s ")
                loge(TAG, getMainThreadStack())
                //todo 回调出去，这里可以按需把其它线程的堆栈也输出
                //todo debug环境可以开一个新进程，弹出堆栈信息
            }
        }

        if (isPause) {
            logi("isPause return")
            return@Runnable
        }

        //如果上一秒没有耗时，重置状态
        if (mainHandlerRunEnd) {
            resetFlagAndsendMainMessage()
        }

        sendDelayThreadMessage()

    }

    private fun sendDelayThreadMessage() {
        mAnrMonitorHandler?.removeCallbacks(mThreadRunnable)
        mAnrMonitorHandler?.postDelayed(
            mThreadRunnable, THREAD_CHCEK_INTERVAL
        )
    }

    @Volatile
    var mainHandlerRunEnd = true

    private val mMainRunnable = Runnable {
        //主线程只是单纯修改这个标志位
        mainHandlerRunEnd = true
    }

    private fun resetFlagAndsendMainMessage() {
        blockTime = 0
        mainHandlerRunEnd = false
        mHadReport = false

        //往主线程post一下消息，然后子线程会1s检测一次，看什么时候这个target 被赋值
        mMainHandler.post {
            mainHandlerRunEnd = true
        }
    }

    fun stop() {
        mAnrMonitorHandler?.removeCallbacks(mThreadRunnable)
        mAnrMonitorThread?.interrupt()
        mAnrMonitorThread = null
    }

    fun pause() {
        isPause = true
        mMainHandler.removeCallbacks(mMainRunnable)
        mAnrMonitorHandler?.removeCallbacks(mThreadRunnable)
    }

    private fun isDebugger(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    private fun getMainThreadStack(): String {
        val mainThread = Looper.getMainLooper().thread
        val mainStackTrace = mainThread.stackTrace
        val sb = StringBuilder()
        for (element in mainStackTrace) {
            sb.append(element.toString())
            sb.append("\r\n")
        }
        return sb.toString()
    }

    companion object {
        const val TAG = "AnrMonitor"
    }
}