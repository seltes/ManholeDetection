//
// Created by Yasu on 2015/12/18.
//

#include "jni.h"

////
//jstring
//Java_i10_manholedetection_ImgFilter_filter(JNIEnv *env,jobject obj,jintArray grayScale,jint width,jint height) {
//    int i,gray;
//    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);
//
//    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
//}

//
jstring
Java_i10_manholedetection_CameraPreview_filter(JNIEnv *env,jobject obj,jintArray grayScale,jbyteArray data,jint width,jint height) {
    int i,gray;
    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);
    jbyte *datas = (*env)->GetByteArrayElements(env, data, 0);
    for (i = 0; i < width * height; i++) {
        gray = datas[i] & 0xff;
        pixels[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
    }

    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
}
