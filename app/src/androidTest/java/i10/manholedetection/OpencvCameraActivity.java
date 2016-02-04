package i10.manholedetection;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OpencvCameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";
    //カメラビューインスタンス
    private CameraBridgeViewBase mCameraView;
    private Mat mOutputFrame;
    private DetectManhole detectManhole;
    int width = 640, height = 480;

    //ライブラリをロード
    static {
        System.loadLibrary("opencv_java3");
    }


    // ライブラリ初期化完了後に呼ばれるコールバック (onManagerConnected)
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                // 読み込みが成功したらカメラプレビューを開始
                case LoaderCallbackInterface.SUCCESS:
                    mCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_view);
        // カメラビューのインスタンスを変数にバインド
        mCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        // リスナーの設定 (後述)
        mCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 非同期でライブラリの読み込み/初期化を行う
        // static boolean initAsync(String Version, Context AppContext, LoaderCallbackInterface Callback)
        Log.d(TAG, "OpenCV library found inside package. Using it!");
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mOutputFrame = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mOutputFrame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        detectManhole=new DetectManhole(inputFrame.gray(),inputFrame.rgba());
        Configuration conf = getResources().getConfiguration();
        if(conf.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Point center = new Point(detectManhole.origin.cols() / 2, detectManhole.origin.rows() / 2);
            Mat matrix = Imgproc.getRotationMatrix2D(center, -90, 1);
            Mat rotatedImg = new Mat();
            Imgproc.warpAffine(detectManhole.origin, rotatedImg, matrix, new Size());
            return rotatedImg;
        }
        else return detectManhole.origin;
    }
}
