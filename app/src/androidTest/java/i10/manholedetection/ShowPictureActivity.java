package i10.manholedetection;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.InputStream;

public class ShowPictureActivity extends Activity {
    private static final int REQUEST_GALLERY = 0;
    private ImageView imgView;
    Bitmap changeImg;
    int width = 640, height = 480;
    int[] pixels = new int[width * height];

    //画像処理　C言語
    static {
        System.loadLibrary("Filter");
    }

    private static native void filter(int[] pixeldata, int width, int height);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        imgView = (ImageView) findViewById(R.id.image_view);
        // ギャラリー呼び出し
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap getImg = BitmapFactory.decodeStream(in);
                assert in != null;
                in.close();

                changeImg = Bitmap.createScaledBitmap(getImg, width, height, false);
                //画像処理
                changeImg.getPixels(pixels, 0, width, 0, 0, width, height);
                filter(pixels, width, height);
                // 選択した画像を表示
                changeImg.setPixels(pixels, 0, width, 0, 0, width, height);
                imgView.setImageBitmap(changeImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
