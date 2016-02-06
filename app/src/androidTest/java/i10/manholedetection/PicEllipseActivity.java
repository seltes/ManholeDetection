package i10.manholedetection;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PicEllipseActivity extends Activity {
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
        Button saveButton = (Button) findViewById(R.id.picSaveButton);
        saveButton.setOnClickListener(saveButton_Click);
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
                detectManhole=new DetectManhole(cvImg,originImg,1);
                changeImg = Bitmap.createBitmap(detectManhole.origin.cols(), detectManhole.origin.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(detectManhole.origin,changeImg);
                //画像出力
                imgView.setImageBitmap(changeImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private View.OnClickListener saveButton_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 画像保存パス
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String imgPath = sf.format(cal.getTime()) + ".png";
            saveAsPngImage(imgPath, changeImg);
        }
    };

    public void saveAsPngImage(String imgPath,Bitmap bitmap) {
        try {
            File extStrageDir = Environment.getExternalStorageDirectory();
            File file = new File(extStrageDir.getAbsolutePath() + "/" + Environment.DIRECTORY_DCIM, imgPath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            registAndroidDB(extStrageDir.getAbsolutePath() + "/" + Environment.DIRECTORY_DCIM + imgPath);
            outStream.close();
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * アンドロイドのデータベースへ画像のパスを登録
     * @param path 登録するパス
     */
    private void registAndroidDB(String path) {
        // アンドロイドのデータベースへ登録
        // (登録しないとギャラリーなどにすぐに反映されないため)
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = PicEllipseActivity.this.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put("_data", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
