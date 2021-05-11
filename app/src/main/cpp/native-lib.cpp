#include <jni.h>
#include <string>
#include "dlopen.h"

#include <android/log.h>
#define LOG_TAG "DeadLockMonitor_native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

void *get_contended_monitor;
void *get_lock_owner_thread;

extern "C" JNIEXPORT jstring JNICALL
Java_com_lizhi_smartlife_mynativeapplication_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}


jint api_level;


const char *get_lock_owner_symbol_name(jint level);

extern "C"
JNIEXPORT jint JNICALL
Java_com_lizhi_smartlife_mynativeapplication_DeadLockMonitor_nativeInit(JNIEnv *env, jobject thiz,jint level) {

    api_level = level;
    // dlopen libart.so

    ndk_init(env);

    void *so_addr = ndk_dlopen("libart.so", RTLD_NOLOAD);
    if (so_addr == NULL) {
        return 1;
    }
    // Monitor::GetContendedMonitor
    //c++方法地址跟c不一样，c++可以重载，方法描述符会变调
    //http://androidxref.com/8.0.0_r4/xref/system/core/libbacktrace/testdata/arm/libart.so
    // nm xxx.so
    // 获取get_contended_monitor 函数，返回值是void*,指向函数的地址
    get_contended_monitor = ndk_dlsym(so_addr, "_ZN3art7Monitor19GetContendedMonitorEPNS_6ThreadE");
    if (get_contended_monitor == NULL) {
        return 2;
    }
    // Monitor::GetLockOwnerThreadId
    get_lock_owner_thread = ndk_dlsym(so_addr, get_lock_owner_symbol_name(api_level));
    if (get_lock_owner_thread == NULL) {
        return 3;
    }
    return 0;

}

const char *get_lock_owner_symbol_name(jint level) {
    if (level < 29) {
        return "_ZN3art7Monitor20GetLockOwnerThreadIdEPNS_6mirror6ObjectE";
    } else {
        //android 10.0，todo
        return "_ZN3art7Monitor20GetLockOwnerThreadIdEPNS_6mirror6ObjectE";
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lizhi_smartlife_mynativeapplication_DeadLockMonitor_getContentThreadIdArt(JNIEnv *env,jobject thiz,jlong native_thread) {

    int monitor_thread_id = 0;
    if (get_contended_monitor != NULL && get_lock_owner_thread != NULL) {
        int monitorObj = ((int (*)(long)) get_contended_monitor)(native_thread);
        if (monitorObj != 0) {
            monitor_thread_id = ((int (*)(int)) get_lock_owner_thread)(monitorObj);
        } else {
            LOGI("GetContendedMonitor return null");
            monitor_thread_id = 0;
        }
    }
    return monitor_thread_id;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lizhi_smartlife_mynativeapplication_DeadLockMonitor_getThreadIdFromThreadPtr(JNIEnv *env,jobject thiz,jlong nativeThread) {

    if (nativeThread != 0) {
        if (api_level > 20) {  // 大于5.0系统
            //reinterpret_cast 强制类型转换
            int *pInt = reinterpret_cast<int *>(nativeThread);
            //地址 +3，就是ThreadId，这个怎么来的呢？
            pInt = pInt + 3;
            return *pInt;  // 返回 monitor 所使用的Thread id
        }
    } else {
        LOGE("suspendThreadArt failed");
    }
    return 0;

}