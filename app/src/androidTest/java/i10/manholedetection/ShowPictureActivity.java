package i10.manholedetection;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Yasu on 2015/12/27.
 */
public class ShowPictureActivity extends Activity {
    private static final int REQUEST_GALLERY = 0;
    private ImageView imgView;
    Bitmap changeImg;
    int width=640,height=480;
    int[] pixels= new  int[width*height];

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
                in.close();

                changeImg = Bitmap.createScaledBitmap(getImg,width,height,false);
                //画像処理
                changeImg.getPixels(pixels, 0, width, 0, 0, width, height);
//                for (int y = 0; y < height; y++) {
//                    for (int x = 0; x < width; x++) {
//                        int idx = x + (y * width);
//                        int red   = pixels[idx] & 0x00ff0000 >> 16;
//                        int green = pixels[idx] & 0x0000ff00 >> 8;
//                        int blue  = pixels[idx] & 0x000000ff;
//                        int gray  = (red + green + blue) / 3;
//                        pixels[idx] = Color.rgb(gray, gray, gray);
//                    }
//                }
                filter(pixels,width,height);
                // 選択した画像を表示
                changeImg.setPixels(pixels,0,width,0,0,width,height);
                imgView.setImageBitmap(changeImg);
            } catch (Exception e) {

            }
        }
    }
}
