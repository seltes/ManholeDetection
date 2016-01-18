package i10.manholedetection;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.fitEllipse;

/**
 * Created by Yasu on 2016/01/18.
 */
public class DetectManhole {
    public Mat img;
    private String TAG = "DetectManhole";
    public DetectManhole(Mat inputImg){
        this.img = inputImg;
        Detection();
    }

    private void Detection() {
        Mat hie = new Mat();
        int i;
        List<MatOfPoint> con = new ArrayList<MatOfPoint>(100);
        List<MatOfPoint2f> pointsf;
        MatOfPoint point;
        MatOfPoint2f point2f = null;
        Mat poMat;
        Imgproc.Canny(img, img, 30, 150);
        ellipseDetect();
//        Imgproc.findContours(img, con, hie, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
//        for(i=0;i<con.size();i++) {
//            Size count = con.get(i).size();
//            if (count.height < 150 || count.height > 1000) {
//                continue;
//            }
//            poMat = new Mat();
//            con.get(i).convertTo(poMat, CvType.CV_32F);
//            point2f = new MatOfPoint2f(poMat);
//            RotatedRect box = Imgproc.fitEllipse(point2f);
//            Imgproc.ellipse(img, box, new Scalar(0, 0, 255), 2);
    }


    private void ellipseDetect() {

        Mat hierarchy = Mat.zeros(new Size(5, 5), CvType.CV_8UC1);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //一番外側のみでOK
        Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
        img = Mat.zeros(new Size(this.img.width(), this.img.height()), CvType.CV_8UC3);
        Scalar color = new Scalar(255, 255, 255);

        Imgproc.drawContours(img, contours, -1, color, 1);

        int i = 0;
        for (i = 0; i < contours.size(); i++) {
            Size count = contours.get(i).size();
            if (count.height < 50 || count.height > 200) {
                continue;
            }
            MatOfPoint ptmat = contours.get(i);
            color = new Scalar(255, 0, 0);
            MatOfPoint2f ptmat2 = new MatOfPoint2f(ptmat.toArray());
            RotatedRect rot = Imgproc.fitEllipse(ptmat2);
            Imgproc.circle(img, rot.center, 5, color, -1);
            color = new Scalar(0, 255, 0);
            Imgproc.ellipse(img, rot, color, 2);
        }
    }
}
