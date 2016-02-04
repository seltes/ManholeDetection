package i10.manholedetection;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;

public class ShowPictureActivity extends Activity {
    private static final int REQUEST_GALLERY = 0;
    private ImageView imgView;
    Bitmap changeImg;
    //処理結果
    Mat cvImg;
    //元画像
    Mat originImg;
    DetectManhole detectManhole;
    int width = 640, height = 480;

    //画像処理　C言語
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Filter");
    }

//    private static native void filter(int[] pixeldata, int width, int height,int cnt);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        imgView = (ImageView) findViewById(R.id.image_view);
        cvImg = new Mat(height,width, CvType.CV_8U);
        originImg = new Mat(height,width, CvType.CV_8U);
        // ギャラリー呼び出し
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap getImg = BitmapFactory.decodeStream(in);
                assert in != null;
                in.close();
                changeImg = Bitmap.createScaledBitmap(getImg, width, height, false);
                Utils.bitmapToMat(changeImg,originImg);
                //C言語での処理
//                changeImg.getPixels(pixels, 0, width, 0, 0, width, height);
//                filter(pixels, width, height, cnt);
//                changeImg.setPixels(pixels, 0, width, 0, 0, width, height);
//                text.setText(String.valueOf(cnt));
                //openCVで用いるMat形式に変換
                Utils.bitmapToMat(changeImg, cvImg);
                Imgproc.cvtColor(cvImg, cvImg, Imgproc.COLOR_RGB2GRAY);
                //マンホール処理
                detectManhole=new DetectManhole(cvImg,originImg);
                changeImg = Bitmap.createBitmap(detectManhole.origin.cols(), detectManhole.origin.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(detectManhole.origin,changeImg);
                //画像出力
                imgView.setImageBitmap(changeImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
