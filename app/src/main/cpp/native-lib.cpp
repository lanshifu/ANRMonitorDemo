#include <jni.h>
#include <string>
#include "dlopen.h"

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
        return ""
    } else {
        return
    }

    return NULL;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lizhi_smartlife_mynativeapplication_DeadLockMonitor_getContentThreadIdArt(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jlong thread_address) {
    return 1;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lizhi_smartlife_mynativeapplication_DeadLockMonitor_getThreadIdFromThreadPtr(JNIEnv *env,
                                                                                      jobject thiz,
                                                                                      jlong thread_address) {
    return 1;

}