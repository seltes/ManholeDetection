package i10.manholedetection;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by i10yasunaga on 2015/12/08.
 */
public class CameraPreviewActivity extends Activity{

    CameraPreview mPreview;
    int numberOfCameras;
    int defaultCameraId;
    int cameraCurrentlyLocked;

    Camera mCamera;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //タイトルを非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // プレビュー画面を作成
        mPreview = new CameraPreview(this);
        setContentView(mPreview);

        // カメラ数を取得
        numberOfCameras = Camera.getNumberOfCameras();

        // 複数カメラがあった場合に備えて、BACKカメラを指定
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // カメラを開く
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // カメラを停止する
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }
}



