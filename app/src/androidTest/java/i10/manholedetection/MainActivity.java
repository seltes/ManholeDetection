package i10.manholedetection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
            intent.setClassName(Manifest.androidPackage,Manifest.cameraPreviewActivity);
            startActivity(intent);
        }
    };

}
