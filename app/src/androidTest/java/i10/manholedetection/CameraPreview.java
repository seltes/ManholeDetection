package i10.manholedetection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

/**
 * Created by i10yasunaga on 2015/12/09.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    Bitmap bmp;
    Activity activity;
    int width = 640, height = 480;  // プレビューの画面サイズ
    int[][] cPixels=new int[width][height];  // ARGB8888の画素の配列
    int[] mGrayResult; //グレースケール
    private byte[] mFrameBuffer;

    //画像処理　C言語
    static {
        System.loadLibrary("Filter");
    }
    private static native void filter(int[] grayScale, byte[] data, int width, int height);


    public CameraPreview(Context context, Activity activity) {
        super(context);
        this.activity = activity;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //カメラ初期化
        mCamera = Camera.open();
        mCamera.setPreviewCallbackWithBuffer(editPreviewImage);
        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            // グレースケール画像　byte (NV21) -> int (ARGB_B888)
            mGrayResult = new int[height * width];
            // 描画するBitmap int (ARGB_8888) -> Bitmap
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            mCamera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(width, height);
            mCamera.setParameters(parameters);
            int size = width * height *
                    ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8;
            mFrameBuffer = new byte[size];
            mCamera.addCallbackBuffer(mFrameBuffer);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }

    private final Camera.PreviewCallback editPreviewImage =
            new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    try {
//                        int[] pixel = mGrayResult;
//                        for (int i = 0; i < width * height; i++) {
//                            int gray = data[i] & 0xff;
//                            pixel[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
//                        }
                        filter(mGrayResult, data, width, height);
                        bmp.setPixels(mGrayResult, 0, width, 0, 0, width, height);
//                        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  // ARGB8888で空のビットマップ作成
//                        cPixels = new int[width*height];
                        Canvas canvas = mHolder.lockCanvas();
                        canvas.drawBitmap(bmp, 0, 0, null);
                        mHolder.unlockCanvasAndPost(canvas);
                        mCamera.addCallbackBuffer(mFrameBuffer);
                    } catch (Exception e) {
                        e.printStackTrace(); // エラー
                        mCamera.stopPreview();
                    }
                }
            };
}

////Cライブラリの作成
//class ImgFilter {
//    private int width, height;
//    private int[] grayScale;
//
//    private static native void filter(int[] grayScale,int width, int height);
//
//    public ImgFilter(int[] grayScale,int w, int h) {
//        this.grayScale=grayScale;
//        this.width = w;
//        this.height = h;
//    }
//
//    public void filter() {
//        filter(this.grayScale,this.width, this.height);
//    }
//}
//
