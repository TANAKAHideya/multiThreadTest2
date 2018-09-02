/*
 * Copyright (C) 2009 The Android Open Source Project
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
#include <stdio.h>
#include <android/log.h>
#include <pthread.h>

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */

#define MAX_THREADS 16

long long sum_func[MAX_THREADS];

struct param {
    int endNum;
    int tno;
};

long long common_func(int x){
    int i;
    long long sum=0;
    for(i = 1;i<=x;i++){
        sum += i;
    }
//	__android_log_print(ANDROID_LOG_ERROR,"sum-jni","common_func %ld %ld\n",i,sum);
    return sum;
}

void func( struct param * x) {
    sum_func[x->tno] = common_func(x->endNum);
//	__android_log_print(ANDROID_LOG_ERROR,"sum-jni","func %x\n",(unsigned int)x);
//	__android_log_print(ANDROID_LOG_ERROR,"sum-jni","func %d %d %lld\n",x->tno, x->endNum, sum_func[x->tno]);
}

jlong
Java_com_example_smpapp_SmpAppActivity_sumFromJNI (JNIEnv* env, jobject thiz, jint endNum, jint tno, jboolean debug)
{
    struct param param[MAX_THREADS];
    pthread_t t[MAX_THREADS];
    int i;
    jlong all_sum=0;

    if(tno>MAX_THREADS){
        return -1;
    }

    for(i=0;i<tno;i++) {
        param[i].endNum = debug ? endNum + i: endNum;
        param[i].tno = i;
//		__android_log_print(ANDROID_LOG_ERROR,"Hello","init %d %d %x\n",param[i].tno, param[i].endNum,(unsigned int)(param+i));
    };
    for(i=0;i<tno;i++) {
        pthread_create( &t[i], NULL, (void *)func, &param[i]);
    }
    for(i=0;i<tno;i++) {
        pthread_join( t[i], NULL );
    }
    for(i=0;i<tno;i++) {
        all_sum += sum_func[i];
    }
    return all_sum;
}
