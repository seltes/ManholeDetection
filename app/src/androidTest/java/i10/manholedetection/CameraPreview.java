package i10.manholedetection;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by i10yasunaga on 2015/12/09.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;

    CameraPreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            mCamera.setPreviewDisplay(holder);
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
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(640, 480);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }


    private boolean mProgressFlag = false;

    private final Camera.PreviewCallback editPreviewImage =
            new Camera.PreviewCallback() {

                public void onPreviewFrame(byte[] data, Camera camera) {
                    mCamera.setPreviewCallback(null);  // プレビューコールバックを解除

                    mCamera.stopPreview();
                    // 画像の保存処理

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MMddHH_mmss_SSS");

                    // 　画像を保存
                    String path = Environment.getExternalStorageDirectory().getPath() +
                            '/' + dateFormat.format(new Date()) + ".raw";

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(path);
                        fos.write(data);
                        fos.close();
                    } catch (IOException e) {
                        Log.e("CAMERA", e.getMessage());
                    }

                    mCamera.startPreview();

                    mProgressFlag = false;
                }
            };
    public void takePreviewRawData() {
        if (!mProgressFlag) {
            mProgressFlag = true;
            mCamera.setPreviewCallback(editPreviewImage);
            //プレビューコールバックをセット
        }
    }
}
