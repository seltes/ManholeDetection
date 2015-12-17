package i10.manholedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by i10yasunaga on 2015/12/09.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    Bitmap bmp;
    int cam_w,cam_h;
    int width,height;  // プレビューの画面サイズ
    int[] rgb;  // ARGB8888の画素の配列

    public CameraPreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        cam_w=640;
        cam_h=480;

    }

    public void surfaceCreated(SurfaceHolder holder) {
        //カメラ初期化
        try {
            mCamera.setPreviewCallback(editPreviewImage);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize( cam_w,cam_h);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: add more exception handling logic here
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
//        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);  // ARGB8888で空のビットマップ作成
        rgb = new int[w*h];
        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.setRotation(90);
        parameters.setPreviewSize(cam_w,cam_h);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }


    private boolean mProgressFlag = false;

    public void takePreviewRawData() {
        if (!mProgressFlag) {
            mProgressFlag = true;
            mCamera.setPreviewCallback(editPreviewImage);
            //プレビューコールバックをセット
        }
    }

    private final Camera.PreviewCallback editPreviewImage =
            new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
//                    mCamera.stopPreview();
                    try {
                        decodeYUV420SP(rgb, data, width, height);  // 変換
                        bmp.setPixels(rgb, 0, width, 0, 0, width, height);  // 変換した画素からビットマップにセット

                        //Cプログラムに渡す
                        int cWidth = bmp.getWidth();
                        int cHeight = bmp.getHeight();
                        int cPixels[] = new int[cWidth * cHeight];
                        bmp.getPixels(cPixels, 0, width, 0, 0, width, height);
                        ImgFilter imgFilter = new ImgFilter(cPixels, cWidth, cHeight);
                        imgFilter.filter();

                        Canvas canvas = mHolder.lockCanvas();
                        canvas.drawBitmap(cPixels, 0, cWidth, 0, 0, cWidth, cHeight, false, null);
                        mHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        mCamera.stopPreview(); // エラー
                    }
                    mCamera.startPreview();
                }
            };

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    //bitmap化用クラス
    public static final void decodeYUV420SP(int[] rgb, byte[]
            yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >>
                        2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    //画像処理　C言語
    static {
        System.loadLibrary("filter");
    }
}

class ImgFilter {
    private int[] pixcels;
    private int width, height;

    private static native void filter(int[] pixcels, int width, int height);

    public ImgFilter(int[] pixs, int w, int h) {
        this.pixcels = pixs;
        this.width = w;
        this.height = h;
    }

    public void filter() {
        filter(this.pixcels, this.width, this.height);
    }
}

