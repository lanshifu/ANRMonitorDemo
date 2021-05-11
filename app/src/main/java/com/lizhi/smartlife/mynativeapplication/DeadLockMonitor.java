package com.lizhi.smartlife.mynativeapplication;

import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * @author lanxiaobin
 * @date 2021/5/11.
 */
public class DeadLockMonitor {

    Object deadLock1 = new Object();
    Object deadLock2 = new Object();

    private String TAG = "DeadLockMonitor";

    public DeadLockMonitor() {
        System.loadLibrary("native-lib");
    }


    void createDeadLock() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (deadLock1) {
                    try {
                        sleep_(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "thread1 wait to get deadLock2,currentThread id = " + Thread.currentThread().getId());
                    synchronized (deadLock2) {
                        Log.e(TAG, "thread1");
                    }
                }
            }
        }, "testThread1");

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (deadLock2) {
                    try {
                        sleep_(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "thread2 wait to get deadLock1,currentThread id = " + Thread.currentThread().getId());
                    synchronized (deadLock1) {
                        Log.e(TAG, "thread2");
                    }
                }
            }
        }, "testThread2");

        thread1.start();
        thread2.start();
    }

    void sleep_(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    Thread[] getAllThreads() {
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        while (currentGroup.getParent() != null) {
            currentGroup = currentGroup.getParent();
        }
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        return lstThreads;
    }

    void startMonitor() {
        int nativeInitResult = nativeInit(Build.VERSION.SDK_INT);
        Log.i(TAG, "nativeInit: " + nativeInitResult);

        Thread[] threads = getAllThreads();
        for (Thread thread : threads) {
            if (thread.getState() == Thread.State.BLOCKED) {
                long threadAddress = (long) ReflectUtil.getField(thread, "nativePeer");
                // 这里记一下，找不到地址，或者线程已经挂了，此时获取到的可能是0和-1
                if (threadAddress <= 0) {
                    continue;
                }

                Log.i(TAG, "thread block ，getId = " + thread.getId() + ",threadAddress=" + threadAddress);

                int blockThreadId = getContentThreadIdArt(threadAddress);

                int curThreadId = getThreadIdFromThreadPtr(threadAddress);

                if (blockThreadId != 0 && curThreadId != 0) {
                    Log.w(TAG, "blockThread = " + blockThreadId + ",curThreadId=" + curThreadId);

                    //todo 最后一步是判断哪个线程造成死锁
//                    deadLock.put(curThreadId, new DeadLockThread(curThreadId, blockThreadId, thread));
                }
            }
        }
    }

    public native int nativeInit(int sdkVersion);
    public native int getContentThreadIdArt(long threadAddress);
    public native int getThreadIdFromThreadPtr(long threadAddress);
}
