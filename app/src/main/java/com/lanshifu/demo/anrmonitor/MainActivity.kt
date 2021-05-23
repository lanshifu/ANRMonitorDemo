package com.lanshifu.demo.anrmonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    val anrMonitor = AnrMonitor(this.lifecycle)

    val deadLockMonitor = DeadLockMonitor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        deadLockMonitor.createDeadLock()

        findViewById<Button>(R.id.startMonitor).setOnClickListener(View.OnClickListener {
            deadLockMonitor.startMonitor()
        })

        findViewById<Button>(R.id.startAnr).setOnClickListener(View.OnClickListener {
            testAnr()
        })
    }

    private fun testAnr(){
        Thread.sleep(6000)
    }



}