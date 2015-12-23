//
// Created by Yasu on 2015/12/18.
//

#include "jni.h"
#include <math.h>

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
    int pix[width*height];
    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);
    jbyte *datas = (*env)->GetByteArrayElements(env, data, 0);
    for (i = 0; i < width * height; i++) {
        pix[i] = datas[i] & 0xff;
//        gray = datas[i] & 0xff;
//        pixels[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
 }

    sobel(pixels,pix,height,width,1);


    (*env)->ReleaseByteArrayElements(env,data,datas,0);
    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
}

void sobel(jintArray *out,jintArray *in, int inHeight, int inWidth, int inChannel){
    int i, j, k, l;
    int weightH[9] = {-1, 0, 1,
                      -2, 0, 2,
                      -1, 0, 1};
    int weightV[9] = {-1, -2, -1,
                      0,  0,  0,
                      1,  2,  1};
    int *imgTmp;
    int weightSize = 3;

    int start = -1;
    int end = start + weightSize;
    double dataTmpH;
    double dataTmpV;
    double dataTmpSum;
    int widthStep;

    widthStep = inWidth * inChannel;
    if(widthStep % 4 != 0){
        widthStep = widthStep + 4 - widthStep % 4;
    }

    for(i = 1; i < (inHeight - 1); ++i){
        for(j = 1; j < (inWidth - 1); ++j){
            dataTmpH = 0;
            dataTmpV = 0;
            for(k = start; k < end; ++k){
                for(l = start; l < end; ++l){
                    // 水平方向
                    dataTmpH += weightH[(k - start) * weightSize + (l - start)] * (unsigned char)in[(i + k) * widthStep + (j + l)];

                    // 垂直方向
                    dataTmpV += weightV[(k - start) * weightSize + (l - start)] * (unsigned char)in[(i + k) * widthStep + (j + l)];

                }
            }
            dataTmpSum = sqrt(dataTmpH * dataTmpH + dataTmpV * dataTmpV);

            // 255を超えた値は255、0未満の値は絶対値
            if(dataTmpSum > 255){
                dataTmpSum = 255;
            } else if(dataTmpSum < 0){
                dataTmpSum = -dataTmpSum;
            }
//            out[i * widthStep + j] = (int)dataTmpSum;
          out[i*widthStep + j] = 0xff000000 | (int)dataTmpSum << 16 | (int)dataTmpSum << 8 |(int)dataTmpSum;
        }
    }
}


