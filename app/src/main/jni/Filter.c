//
// Created by Yasu on 2015/12/18.
//

#include "jni.h"
#include <math.h>

int width, height, size;

void Java_i10_manholedetection_ShowPictureActivity_filter(JNIEnv *env, jobject obj, jintArray grayScale,
                                                     jint inWidth, jint inHeight);
void ExtractGray(int out[],jint *in);
void Gaussian(int out[],int in[]);
void Sobel(int out[],int in[],int step);
void ExtractRoad(int out[],int in[]);
void Threshold(int out[],int in[],int threshold);
void OutputResult(jint *out,int in[]);
void EmphasisEdge(int out[],int in[]);
double  GausFunc(int x,int y,double sig);


void Java_i10_manholedetection_ShowPictureActivity_filter(JNIEnv *env, jobject obj, jintArray grayScale,
                                                             jint inWidth, jint inHeight) {
    width = inWidth;
    height = inHeight;
    size = width * height;
    int road[size];
    int gray[size];
    int gaus[size];
    int edge[size];
    int emph[size];
    int thre[size];

    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);

    //グレースケール化
    ExtractGray(gray, pixels);
    //ガウシアンフィルタ
    Gaussian(gaus, gray);
    //エッジ強調
    EmphasisEdge(emph,gray);
    //エッジ抽出
    Sobel(edge, emph, 2);
//    //参考　岡山理科大　道路情景画像からの路面表示の抽出と認識
//    ExtractRoad(road, gaus);
    //二値化
    Threshold(thre, edge,200);

    //出力
    OutputResult(pixels, gaus);
    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
}

jstring
Java_i10_manholedetection_CameraPreview_filter(JNIEnv *env, jobject obj, jintArray grayScale,
                                               jbyteArray data, jint width, jint height) {
    int i, gray;
    int pix[width * height];
    jint *pixels = (*env)->GetIntArrayElements(env, grayScale, 0);
    jbyte *datas = (*env)->GetByteArrayElements(env, data, 0);
    for (i = 0; i < width * height; i++) {
        pix[i] = datas[i] & 0xff;
//        gray = datas[i] & 0xff;
//        pixels[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
    }

    Sobel(pixels, pix, 1);

    (*env)->ReleaseByteArrayElements(env, data, datas, 0);
    (*env)->ReleaseIntArrayElements(env, grayScale, pixels, 0);
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

void Sobel(int out[], int in[], int inChannel) {
    int i, j, k, l;
    int weightH[9] = {-1, 0, 1,
                      -2, 0, 2,
                      -1, 0, 1};
    int weightV[9] = {-1, -2, -1,
                      0, 0, 0,
                      1, 2, 1};
    int *imgTmp;
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
//            out[i * widthStep + j] = (int)dataTmpSum;
            out[i * widthStep + j] =
                    0xff000000 | (int) dataTmpSum << 16 | (int) dataTmpSum << 8 | (int) dataTmpSum;
        }
    }
}

//void Gaussian(int out[], int in[]) {
//    int x, y, r;
//    int gx, gy;
//    int offset;
//    int pixel;
//    int result;
//    int weightSize = 3;
//    int start = -1;
//    int end = start + weightSize;
//    int sumWeight = 9;
//    double sumKanel=0.0;
//    int weight[9] = {1, 2, 1,
//                     2, 4, 2,
//                     1, 2, 1};
//        for (y = 1; y < (height - 1); y++) {
//            for (x = 1; x < (width - 1); x++) {
//                offset = x + y * width;
//                result = 0;
//                sumKanel =0.0;
//                for (gy = start; gy < end; gy++) {
//                    for (gx = start; gx < end; gx++) {
//                        pixel = in[gx - 1 + (gy - 1) * width + offset];
//                        result += pixel;
////                        result += pixel * weight[(gy - start) * weightSize + (gx - start)];
////                        sumKanel += GausFunc(x,y,1.4);
//                    }
//                }
//                result /= sumWeight;
//                // 255を超えた値は255、0未満の値は絶対値
//                if (result > 255) {
//                    result = 255;
//                } else if (result < 0) {
//                    result = -result;
//                }
//                out[offset] = result;
//            }
//        }
//}

double GausFunc(int x,int y,double sig){
    return ((1.0/2.0*M_PI*sig*sig)*exp(-((x^2+y^2)/2.0*sig*sig)));
}

void Threshold(int out[], int in[], int threshold) {
    int i, thr;
    for (i = 0; i < width * height; i++) {
        thr = in[i] >= threshold ? 255 : 0;
        out[i] = thr;
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

/**
 * 路面抽出
 */
void ExtractRoad(int out[], int in[]) {
    int x, y, i, gx, gy;
    int offset;
    int pixels[9];
    int no[] = {4, 3, 2, 5, 0, 1, 6, 7, 8};
    int flag;
    int n;

    //輪郭線抽出
    for (y = 1; y < (height - 1); y++) {
        for (x = 1; x < (width - 1); x++) {
            offset = x + y * width;
            i = 0;
            for (gy = 0; gy < 3; gy++) {
                for (gx = 0; gx < 3; gx++) {
                    pixels[no[i]] = in[gx - 1 + (gy - 1) * width + offset];
                    i++;
                }
            }
            flag = 0;
            for (i = 1; i <= 4; i++) {
                if (pixels[i] <= pixels[0] & pixels[i + 4] <= pixels[0]) {
                    flag++;
                }
            }
            out[offset] = flag == 4 ? 1 : 0;
        }
    }
//    for (y = 1; y < (height - 1); y++) {
//        for (x = 1; x < (width - 1); x++) {
//            offset = x + y * width;
//            i = 0;
//            for (gy = 0; gy < 3; gy++) {
//                for (gx = 0; gx < 3; gx++) {
//                    pixels[no[i]] = 1 - out[gx - 1 + (gy - 1) * width + offset];
//                    i++;
//                }
//            }
//            //8近傍連結
//            for (i = 0; i <= 2; i++) {
//                n += pixels[2 * i + 1] - pixels[2 * i + 1] * pixels[2 * i + 2] * pixels[2 * i + 3];
//            }
//            n += pixels[7] - pixels[7] * pixels[8] * pixels[1];
//            if (n <= 1) {
//                out[offset] = 0;
//            }
//        }
//    }
}

void Label(int out[],int in[]){

}

void EmphasisEdge(int out[],int in[]){
    int emp[]={-2,-5,-2,
               -5,32,-5,
               -2,-5,-2};
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
               2,8,2,
               1,2,1};
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
                    result += pixel * weight[(gy - start) * weightSize + (gx - start)]/16;
                }
            }
            result/=16;
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




