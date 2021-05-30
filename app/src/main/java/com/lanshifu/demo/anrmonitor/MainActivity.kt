package com.lanshifu.demo.anrmonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    val anrMonitor = AnrMonitor(this.lifecycle)

    val deadLockMonitor = DeadLockMonitor()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        DeadLockUtil.createDeadLock()

        findViewById<Button>(R.id.startMonitor).setOnClickListener(View.OnClickListener {
            deadLockMonitor.startMonitor()
        })

        findViewById<Button>(R.id.startAnr).setOnClickListener(View.OnClickListener {
            testAnr()
        })
    }

    private fun testAnr(){
        DeadLockUtil.createDeadLockAnr()
    }

    private fun testDeadLockAnr() {

    }


}