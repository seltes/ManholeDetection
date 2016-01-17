//
// Created by Yasu on 2015/12/18.
//

#include "jni.h"
#include <math.h>

#define HIGH 255
#define L_BASE 100

int width, height, size;

void Java_i10_manholedetection_ShowPictureActivity_filter(JNIEnv *env, jobject obj, jintArray grayScale,
                                                     jint inWidth, jint inHeight,jint cnt);
void ExtractGray(int out[],jint *in);
void ExtractGrayB(int out[],jbyte *in);
void Gaussian(int out[],int in[]);
void Sobel(int out[],int in[],int step);
void Threshold(int out[],int in[],int threshold);
void OutputResult(jint *out,int in[]);
void EmphasisEdge(int out[],int in[]);
void Label(int out[],int in[],int cnt);
void LabelSet(int lab[], int x, int y, int label);


void Java_i10_manholedetection_ShowPictureActivity_filter(JNIEnv *env, jobject obj, jintArray grayScale,
                                                             jint inWidth, jint inHeight,jint cnt) {
    width = inWidth;
    height = inHeight;
    size = width * height;
    int i[size];
    int j[size];
    int k[size];
    int result[size];

    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);

    //グレースケール化
    ExtractGray(i, pixels);
    //ガウシアンフィルタ
    Gaussian(j, i);
    //エッジ抽出
    Sobel(k, j, 2);
//    //エッジ強調
//    EmphasisEdge(i,k);
    //二値化
    Threshold(result, k,30);
//    //ラベリング
//    Label(result,j,cnt);

    //出力
    OutputResult(pixels, result);
    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
}

void
Java_i10_manholedetection_CameraPreview_filter(JNIEnv *env, jobject obj, jintArray grayScale,
                                               jbyteArray data, jint inWidth, jint inHeight) {
    width = inWidth;
    height = inHeight;
    size = width * height;
    int i[size];
    int j[size];
    int k[size];

    jbyte *datas = (*env)->GetByteArrayElements(env, data, 0);
    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);

    //グレースケール化
    ExtractGrayB(i, datas);
    //ガウシアンフィルタ
    Gaussian(j, i);
    //エッジ抽出
    Sobel(k, j, 2);
    //エッジ強調
    EmphasisEdge(i,k);
    //二値化
    Threshold(j, i,180);

//    Label(labe,thre);
//
//    Threshold(thre,labe,1,1);

    //出力
    OutputResult(pixels, j);
    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
    (*env)->ReleaseByteArrayElements(env, data, datas, 0);
}

/**
 * グレースケールの生成
 */
void ExtractGray(int out[], jint *in) {
    int i;
    double r,g,b;
    int ymax=0,ymin=0;
    for(i=0;i<size;i++){
        r = 0.3 * (in[i] & 0x00ff0000 >> 16);
        g = 0.59*(in[i] & 0x0000ff00 >> 8);
        b = 0.11*(in[i] & 0x000000ff);
        out[i] = (int)(r + g + b);
        if(out[i]>ymax){
            ymax=out[i];
        }
        if(out[i]<ymin){
            ymin=out[i];
        }
    }
    for(i=0;i<size;i++){
        out[i]=255*(out[i]-ymin)/(ymax-ymin);
    }
//    for (i = 0; i < size; i++) {
//        out[i] = in[i] & 0xff;
//    }
}

void ExtractGrayB(int out[], jbyte *in) {
    int i;
    double r,g,b;
    int ymax=0,ymin=0;
    for(i=0;i<size;i++){
        r = 0.3 * (in[i] & 0x00ff0000 >> 16);
        g = 0.59*(in[i] & 0x0000ff00 >> 8);
        b = 0.11*(in[i] & 0x000000ff);
        out[i] = (int)(r + g + b);
        if(out[i]>ymax){
            ymax=out[i];
        }
        if(out[i]<ymin){
            ymin=out[i];
        }
    }
    for(i=0;i<size;i++){
        out[i]=255*(out[i]-ymin)/(ymax-ymin);
    }
}

void Sobel(int out[], int in[], int inChannel) {
    int i, j, k, l;
    int weightH[9] = {-1, 0, 1,
                      -2, 0, 2,
                      -1, 0, 1};
    int weightV[9] = {-1, -2, -1,
                      0, 0, 0,
                      1, 2, 1};
    int weightSize = 3;

    int start = -1;
    int end = start + weightSize;
    double dataTmpH;
    double dataTmpV;
    double dataTmpSum;
    int widthStep;

    widthStep = width * inChannel;
    if (widthStep % 4 != 0) {
        widthStep = widthStep + 4 - widthStep % 4;
    }

    for (i = 1; i < (height - 1); ++i) {
        for (j = 1; j < (width - 1); ++j) {
            dataTmpH = 0;
            dataTmpV = 0;
            for (k = start; k < end; ++k) {
                for (l = start; l < end; ++l) {
                    // 水平方向
                    dataTmpH += weightH[(k - start) * weightSize + (l - start)] *
                                (unsigned char) in[(i + k) * widthStep + (j + l)];

                    // 垂直方向
                    dataTmpV += weightV[(k - start) * weightSize + (l - start)] *
                                (unsigned char) in[(i + k) * widthStep + (j + l)];

                }
            }
            dataTmpSum = sqrt(dataTmpH * dataTmpH + dataTmpV * dataTmpV);

            // 255を超えた値は255、0未満の値は絶対値
            if (dataTmpSum > 255) {
                dataTmpSum = 255;
            } else if (dataTmpSum < 0) {
                dataTmpSum = -dataTmpSum;
            }
            out[i * widthStep + j] = (int)dataTmpSum;
//            out[i * widthStep + j] =
//                    0xff000000 | (int) dataTmpSum << 16 | (int) dataTmpSum << 8 | (int) dataTmpSum;
        }
    }
}

void Threshold(int out[], int in[], int threshold) {
    int i, thr;
    for (i = 0; i < width * height; i++) {
        thr = in[i] >= threshold ? 255 : 0;
        out[i] = thr;
    }
}



void EmphasisEdge(int out[],int in[]){
//    int emp[]={-2,-5,-2,
//               -5,32,-5,
//               -2,-5,-2};
//    int sumEmp = 4;
    int emp[]={-2,-3,-2,
               -3,32,-3,
               -2,-3,-2};
//    int sumEmp = 12;
    int x,y,offset,gy,gx,result,pixel;
    int weightSize = 3;
    int start = -1;
    int end = start + weightSize;
    for (y = 1; y < (height - 1); y++) {
        for (x = 1; x < (width - 1); x++) {
            offset = x + y * width;
            result = 0;
            for (gy = start; gy < end; gy++) {
                for (gx = start; gx < end; gx++) {
                    pixel = (unsigned char)in[gx - 1 + (gy - 1) * width + offset];
                    result += pixel * emp[(gy - start) * weightSize + (gx - start)];
                }
            }
            result /= 4;
            // 255を超えた値は255、0未満の値は絶対値
            if (result > 255) {
                result = 255;
            } else if (result < 0) {
                result = -result;
            }
            out[offset] = result;
        }
    }
}

void Gaussian(int out[],int in[]){
    int weight[]={1,2,1,
                  2,4,2,
                  1,2,1};
    int sumWeight = 16;
    int weightSize = 3;
    int start = -1;
//    int weight[] = {1, 4, 6, 4,1,
//                    4,16,24,16,4,
//                    6,24,36,24,6,
//                    4,16,24,16,4,
//                    1, 4, 6, 4,1};
//    int sumWeight = 256;
//    int weightSize = 5;
//    int start = -2;
    int x,y,offset,gy,gx,result,pixel;
    int end = start + weightSize;
    for (y = 1; y < (height + start); y++) {
        for (x = 1; x < (width + start); x++) {
            offset = x + y * width;
            result = 0;
            for (gy = start; gy < end; gy++) {
                for (gx = start; gx < end; gx++) {
                    pixel = (unsigned char)in[gx + start + (gy + start) * width + offset];
                    result += pixel * weight[(gy - start) * weightSize + (gx - start)]/sumWeight;
                }
            }
            // 255を超えた値は255、0未満の値は絶対値
            if (result > 255) {
                result = 255;
            } else if (result < 0) {
                result = -result;
            }
            out[offset] = result;
        }
    }
}

void Label(int out[],int in[],int cnt) {
    int x, y, label;
    label = L_BASE;
    for (y = 0; y < size; y++) {
        out[y] = in[y];
    }
    for (y = 0; y < height; y++) {
        for (x = 0; x < width; x++) {
            if (out[x + y * width] == HIGH) {
                if (label < HIGH) {
                    LabelSet(out, x, y, label);
                    label++;
                }
            }
        }
    }
    cnt = label - L_BASE;
}

void LabelSet(int image[], int xs, int ys, int label) {
    int x, y, cnt, offset, gx, gy;
    image[xs + width * ys] = label;
    for (; ;) {
        cnt = 0;
        for (y = 1; y < height - 1; y++) {
            for (x = 1; x < width - 1; x++) {
                if (image[x + y * width] == label) {
                    offset = x + y * width;
                    for (gy = 0; gy < 3; gy++) {
                        for (gx = 0; gx < 3; gx++) {
                            if(image[gx - 1 + (gy - 1) * width + offset] == HIGH){
                                image[gx - 1 + (gy - 1) * width + offset] = label;
                                cnt++;
                            }
                        }
                    }
                }
            }
        }
        if (cnt == 0) break;
    }
}

/**
 * グレイスケール画像の作成
 */
void OutputResult(jint *out, int in[]) {
    int i;
    for (i = 0; i < width * height; i++) {
        out[i] = 0xff000000 | in[i] << 16 | in[i] << 8 | in[i];
    }
}

