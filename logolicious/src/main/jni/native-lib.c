/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <jni.h>
#include<string.h>

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   hello-jni/app/src/main/java/com/example/hellojni/HelloJni.java
 */
JNIEXPORT jstring JNICALL
                  Java_com_olav_logolicious_util_GlobalClass_stringFromJNI(JNIEnv *env,
                                                                           jobject thiz) {
#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
    #if defined(__ARM_PCS_VFP)
    #define ABI "armeabi-v7a/NEON (hard-float)"
    #else
    #define ABI "armeabi-v7a/NEON"
    #endif
    #else
    #if defined(__ARM_PCS_VFP)
    #define ABI "armeabi-v7a (hard-float)"
    #else
    #define ABI "armeabi-v7a"
    #endif
    #endif
    #else
    #define ABI "armeabi"
    #endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif

    return (*env)->NewStringUTF(env, "C++  Compiled with ABI " ABI ".");
}

//JNIEXPORT jstring JNICALL
//Java_com_olav_logolicious_util_GlobalClass_getCurrentAlloc(JNIEnv *env,
//                                                         jobject thiz) {
//
//    return (*env)->NewIntUTF(env, "Current alloc " + currentAlloc + ".");
//}

JNIEXPORT void JNICALL
Java_com_olav_logolicious_util_GlobalClass_dlmalloc(JNIEnv * env, jobject _this)
{
size_t sz = 7077888; //1024 = mb
char* a = (char*)malloc(7077888);
strcpy(a,"DougLeaMalloc");
free(a);
}

static char* blob = NULL;
static int maxToAlloc = 100 * 1024 * 1024;
static int currentAlloc = 0;

JNIEXPORT jint JNICALL
        Java_com_olav_logolicious_util_GlobalClass_malloc(JNIEnv * env, jobject this, jint bytes)
{

if(currentAlloc < maxToAlloc) {
blob = (char*) malloc(sizeof(char) * bytes);
memset(blob, '\0', sizeof(char) * bytes);
currentAlloc = currentAlloc + bytes;
}

if (NULL == blob) {
//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "Failed to allocate memory\n");
} else {
char m[50];
//sprintf(m, "Allocated %d bytes", sizeof(char) * bytes);
//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, m);
}
}

JNIEXPORT jint JNICALL
        Java_com_olav_logolicious_util_GlobalClass_assigned_bytes_to_malloc(JNIEnv * env, jobject this, jint bytes)
{
memset(blob, '\0', sizeof(char) * bytes);
}

JNIEXPORT void JNICALL
Java_com_olav_logolicious_util_GlobalClass_freeMem(JNIEnv * env, jobject this)
{
free(blob);
blob = NULL;
}

JNIEXPORT int JNICALL
Java_com_olav_logolicious_util_GlobalClass_freeMemSize(JNIEnv * env, jobject this, jint bytes)
{
free(bytes);
blob = NULL;
}