//
// Created by Yasu on 2015/12/18.
//

#include "jni.h"
#include <math.h>


jstring
Java_i10_manholedetection_ShowPictureActivity_filter(JNIEnv *env,jobject obj,jintArray grayScale,jint width,jint height) {
    int x, y;
    int gray[width * height];
    int gaus[width * height];
    int edge[width * height];
    int thre[width * height];

    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);

    int ntk;

    for (y = 0; y < height; y++) {
        for (x = 0; x < width; x++) {
            gray[x + y * width] = pixels[x + y * width] & 0xff;
        }
    }
    Gaussian(gaus, gray, height, width);
    Gaussian(gaus, gaus, height, width);
    Sobel(edge, gaus, height, width, 2);


    Threshold(thre,edge,height,width);
    OutputResult(pixels,edge,height,width);
    (*env)->ReleaseIntArrayElements(env,grayScale,pixels,0);
}

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

    Sobel(pixels, pix, height, width, 1);

    (*env)->ReleaseByteArrayElements(env,data,datas,0);
    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
}

void Sobel(jint *out, jint *in, int inHeight, int inWidth, int inChannel){
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

void Gaussian(int out[],int in[],int inHeight, int inWidth){
    int x,y;
    int gx,gy;
    int offset;
    int pixel;
    int result;
    int weight[9] = {1, 2, 1,
                      2, 4, 2,
                      1, 2, 1};

    for(y = 1;  y < (inHeight - 1); y++){
        for(x = 1; x < (inWidth - 1); x++){
            offset= x+y*inWidth;
            result = 0;
            for(gy = 0 ; gy < 3; gy++) {
                for (gx = 0; gx < 3; gx++) {
                    pixel = in[gx-1 + (gy-1)*inWidth+offset];
                    result += pixel * weight[gx*gy];
                }
            }
            out[offset] = result/16;
        }
    }
}

void Threshold(int out[],int in[],int height,int width){
    int i,thr;
    for (i = 0; i < width * height; i++) {
        thr = in[i] & 0xff;
        if(thr >150){
            thr=255;
        }
        else{
            thr = 0;
        }
        out[i]=thr;
    }
}

void OutputResult(jint *out,int in[],int height,int width){
    int i;
    for(i = 0;i < width * height;i++){
        out[i] = 0xff000000 | in[i] << 16 | in[i] << 8 | in[i];
    }
}




