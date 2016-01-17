package i10.manholedetection;

import android.support.v4.graphics.ColorUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yasu on 2016/01/18.
 */
public class DetectManhole {
    public Mat img;

    public DetectManhole(Mat inputImg){
        this.img = inputImg;
        Detection();
    }

    private void Detection(){
        Mat hie=new Mat();
        int i;
        List con =new ArrayList<MatOfPoint>(100);
        MatOfPoint2f point2f = null;
        Imgproc.Canny(img, img, 30, 150);
        Imgproc.findContours(img, con, hie, Imgproc.CV_RETR_LIST, Imgproc.CV_CHAIN_APPROX_NONE);
//        for(i=0;i<con.size();i++) {
//            RotatedRect rot = Imgproc.fitEllipse();
//            Imgproc.ellipse(img,rot, new Scalar(0,0,255),2,8);
//        }
        // ビット反転
//        Core.bitwise_not(img, img);
    }
}
