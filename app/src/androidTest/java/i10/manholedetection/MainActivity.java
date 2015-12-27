package i10.manholedetection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static i10.manholedetection.StringsFile.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
    }

    private void setViews() {
        Button camButton = (Button) findViewById(R.id.camButton);
        Button picButton = (Button) findViewById(R.id.picButton);
        camButton.setOnClickListener(camButton_Click);
        picButton.setOnClickListener(picButton_Click);
    }

    private View.OnClickListener camButton_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(Manifest.androidPackage,Manifest.cameraPreviewActivity);
            startActivity(intent);
        }
    };

    private View.OnClickListener picButton_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName(Manifest.androidPackage,Manifest.showPictureActivity);
            startActivity(intent);
        }
    };

}
