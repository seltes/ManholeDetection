//
// Created by i10yasunaga on 2015/11/30.
//


#include "jni.h"


void Java_i10_ManholeDetection_MainActivity_mainTest(JNIEnv * env, jobject  obj,  jintArray src, jint width, jint height)
{
    int i, totalPixel;
    jint* arr  =(*env)->GetIntArrayElements(env,src,0);
    totalPixel = width * height;
    for(i=0; i< totalPixel; i++) {
        int alpha = (arr[i] & 0xFF000000) >> 24;
        int red   = (arr[i] & 0x00FF0000) >> 16;
        int green = (arr[i] & 0x0000FF00) >> 8;
        int blue  = (arr[i] & 0x000000FF);
        //ここに計算処理を色々と書く。
        arr[i] = (alpha << 24) | (red << 16) | (green << 8)| blue;
    }
    (*env)->ReleaseIntArrayElements(env, src, arr, 0);
}