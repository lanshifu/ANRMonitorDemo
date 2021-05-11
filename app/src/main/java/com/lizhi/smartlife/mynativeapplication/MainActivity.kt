package com.lizhi.smartlife.mynativeapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    val deadLockMonitor = DeadLockMonitor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        deadLockMonitor.createDeadLock()


        findViewById<Button>(R.id.startMonitor).setOnClickListener(View.OnClickListener {
            deadLockMonitor.startMonitor()
        })
    }


}