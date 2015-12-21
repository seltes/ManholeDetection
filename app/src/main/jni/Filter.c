//
// Created by Yasu on 2015/12/18.
//

#include "../../../../../../AppData/Local/Android/sdk/ndk-bundle/platforms/android-21/arch-arm/usr/include/jni.h"

//
//void *encodeBitmap(JNIEnv *env, jobject obj,jintArray src , jbyteArray byte, jint width,
//                   jint height) {
//    int frameSize = width * height;
//    int i, j, yp;
//    jint *rgb = (*env)->GetIntArrayElements(env, src, 0);
//    jbyte *yuv420sp = (*env)->GetByteArrayElements(env, byte, 0);
//    for (j = 0, yp = 0; j < height; j++) {
//        int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
//        for (i = 0; i < width; i++, yp++) {
//            int y = (0xff & ((int) yuv420sp[yp])) - 16;
//            if (y < 0) y = 0;
//            if ((i & 1) == 0) {
//                v = (0xff & yuv420sp[uvp++]) - 128;
//                u = (0xff & yuv420sp[uvp++]) - 128;
//            }
//
//            int y1192 = 1192 * y;
//            int r = (y1192 + 1634 * v);
//            int g = (y1192 - 833 * v - 400 * u);
//            int b = (y1192 + 2066 * u);
//
//            if (r < 0) r = 0;
//            else if (r > 262143) r = 262143;
//            if (g < 0) g = 0;
//            else if (g > 262143) g = 262143;
//            if (b < 0) b = 0;
//            else if (b > 262143) b = 262143;
//
//            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >>
//                                                             2) & 0xff00) | ((b >> 10) & 0xff);
//        }
//    }
//    (*env)->ReleaseIntArrayElements(env, src,rgb, 0);
//}
//
//bitmap変換込
jstring
Java_i10_manholedetection_ImgFilter_filter(JNIEnv *env,jobject obj,jintArray grayScale,jint width,jint height) {
    int i,gray;
    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);

    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
}

//jstring
//Java_i10_manholedetection_ImgFilter_filter(JNIEnv *env,jobject obj,jintArray src,jint width,jint height) {
//    int i, totalPixel;
//    jint *arr = (*env)->GetIntArrayElements(env, src, 0);
//    totalPixel = width * height;
//    for (i = 0; i < totalPixel; i++) {
//        int alpha = (arr[i] & 0xFF000000) >> 24;
//        int red = (arr[i] & 0x00FF0000) >> 16;
//        int green = (arr[i] & 0x0000FF00) >> 8;
//        int blue = (arr[i] & 0x000000FF);
//        //ここから処理
//        green = 0;
//        blue = 0;
//        arr[i] = (alpha << 24) | (red << 16) | (green << 8) |
//                 blue;
//    }
//    (*env)->ReleaseIntArrayElements(env, src, arr, 0);
//}

