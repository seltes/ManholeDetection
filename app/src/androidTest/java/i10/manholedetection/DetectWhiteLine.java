package i10.manholedetection;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Yasu on 2016/02/04.
 */
public class DetectWhiteLine {

    public Mat img;
    public Mat origin;
    private String TAG = "DetectManhole";

    public DetectWhiteLine(Mat inputImg, Mat origin) {
        this.img = inputImg;
        this.origin = origin;
        Detection();
    }

    private void Detection(){
        Imgproc.threshold(img,img,180,255,Imgproc.THRESH_BINARY);
        Imgproc.Canny(img,origin,50,200);
    }
}
