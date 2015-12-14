package i10.manholedetection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static i10.manholedetection.StringsFile.*;

import i10.manholedetection.R;

public class MainActivity extends AppCompatActivity {

    //カメラプレビュークラス
    private CameraPreviewActivity cameraPreviewActivity = null;

    private int[] pixcels;
    private int width, height;

    private static native void mainTest(int[] pixcels, int width, int height); //宣言

    /**
     * filter
     */
    public void mainTest() {
        mainTest(this.pixcels, this.width, this.height);
    }

    //camera
    private Uri m_uri;
    private static final int REQUEST_CHOOSER = 1000;

//    private void showGallery() {
//
//        //カメラの起動Intentの用意
//        String photoName = System.currentTimeMillis() + ".jpg";
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, photoName);
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        m_uri = getContentResolver()
//                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//
//        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri);
//
//        // ギャラリー用のIntent作成
//        Intent intentGallery;
//        if (Build.VERSION.SDK_INT < 19) {
//            intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
//            intentGallery.setType("image/*");
//        } else {
//            intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
//            intentGallery.setType("image/jpeg");
//        }
//        Intent intent = Intent.createChooser(intentCamera, "画像の選択");
//        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentGallery});
//        startActivityForResult(intent, REQUEST_CHOOSER);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CHOOSER) {
//
//            if (resultCode != RESULT_OK) {
//                // キャンセル時
//                return;
//            }
//
//            Uri resultUri = (data != null ? data.getData() : m_uri);
//
//            if (resultUri == null) {
//                // 取得失敗
//                return;
//            }
//
//            // ギャラリーへスキャンを促す
//            MediaScannerConnection.scanFile(
//                    this,
//                    new String[]{resultUri.getPath()},
//                    new String[]{"image/jpeg"},
//                    null
//            );
//
//            // 画像を設定
//            ImageView imageView = (ImageView) findViewById(R.id.image_view);
//            imageView.setImageURI(resultUri);
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
    }

    private void setViews() {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(button_onClick);
    }

    private View.OnClickListener button_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(MainActivity.this,Manifest.cameraPreviewActivity);
            startActivity(intent);
        }
    };

}
