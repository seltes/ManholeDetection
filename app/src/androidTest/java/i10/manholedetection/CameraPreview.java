package i10.manholedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by i10yasunaga on 2015/12/09.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    Bitmap bmp;
    int cam_w,cam_h;
    int width,height;  // プレビューの画面サイズ
    int[] cPixels;  // ARGB8888の画素の配列

    //画像処理　C言語
    static {
        System.loadLibrary("filter");
    }

    public CameraPreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        cam_w=480;
        cam_h=640;

    }

    public void surfaceCreated(SurfaceHolder holder) {
        //カメラ初期化
        try {
            mCamera.setPreviewCallback(editPreviewImage);
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
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview
        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.setRotation(90);
        parameters.setPreviewSize(cam_h, cam_w);
//        parameters.setPreviewSize(h,w);
        width=cam_h;
        height=cam_w;
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
//        mCamera.setDisplayOrientation(90);
    }

    private final Camera.PreviewCallback editPreviewImage =
            new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    try {
                        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  // ARGB8888で空のビットマップ作成
                        cPixels = new int[width*height];

                        //エンコードBitmap,画像処理部(C）
                        ImgFilter imgFilter=new ImgFilter(cPixels,data,width,height);
                        imgFilter.filter();

//                        decodeYUV420SP(cPixels, data, width, height);  // 変換
//                        bmp.setPixels(cPixels, 0, width, 0, 0, width, height);  // 変換した画素からビットマップにセット

                        //画像処理部（C）
//                        int cWidth = bmp.getWidth();
//                        int cHeight = bmp.getHeight();
//                        int cPixels[] = new int[cWidth * cHeight];
//                        bmp.getPixels(cPixels, 0, width, 0, 0, width, height);
//                        ImgFilter imgFilter = new ImgFilter(cPixels, cWidth, cHeight);
//                        imgFilter.filter();

                        Canvas canvas = mHolder.lockCanvas();
                        canvas.drawBitmap(cPixels, 0, width, 0, 0, width, height, false, null);
                        mHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
//                        mCamera.stopPreview(); // エラー
                    }
                }
            };

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
}

//Cライブラリの作成
class ImgFilter {
    private int[] pixels;
    private byte[] data;
    private int width, height;

    private static native void filter(int[] pixels,byte[] data,int width, int height);

    public ImgFilter(int[] pixels,byte[] data,int w, int h) {
        this.pixels = pixels;
        this.data = data;
        this.width = w;
        this.height = h;
    }

    public void filter() {
        filter(this.pixels,this.data, this.width, this.height);
    }
}

//class EncodeBitmap{
//    private int[] cPixels;
//    private byte[] data;
//    private int width, height;
//
//    private static native void encodeBitmap(int[] cPixels,byte[] data, int width, int height);
//
//    public EncodeBitmap(int[] cPixels,byte[] data,int w, int h) {
//        this.cPixels = cPixels;
//        this.data = data;
//        this.width = w;
//        this.height = h;
//    }
//
//    public void encodeBitmap() {
//        encodeBitmap(this.cPixels, this.data, this.width, this.height);
//    }
//}

