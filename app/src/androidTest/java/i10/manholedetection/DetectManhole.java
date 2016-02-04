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
import org.opencv.ml.StatModel;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.fitEllipse;

/**
 * Created by Yasu on 2016/01/18.
 */
public class DetectManhole {
    public Mat img;
    public Mat origin;
    private String TAG = "DetectManhole";

    public DetectManhole(Mat inputImg, Mat origin) {
        this.img = inputImg;
        this.origin = origin;
        Detection();
    }

    private void Detection() {
        Mat dec = new Mat();
        int i;
        List<MatOfPoint> con = new ArrayList<MatOfPoint>(100);
        List<MatOfPoint2f> pointsf;
        MatOfPoint point;
        MatOfPoint2f point2f = null;
//      Cannyフィルタ
        Imgproc.Canny(img, img, 10, 60);
//      膨張
        Imgproc.dilate(img, img, new Mat());
        ellipseDetect();
////       origin = img;
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
            if (count.height < 80 || count.height > 400) {
                continue;
            }
            MatOfPoint ptmat = contours.get(i);
            color = new Scalar(255, 0, 0);
            MatOfPoint2f ptmat2 = new MatOfPoint2f(ptmat.toArray());
            RotatedRect rot = Imgproc.fitEllipse(ptmat2);
            Size size = rot.boundingRect().size();
            if(checkEllipse(size,rot)){
                Imgproc.circle(origin, rot.center, 5, color, -1);
                color = new Scalar(0, 255, 0);
                Imgproc.ellipse(origin, rot, color, 2);
            }
        }
    }

    boolean checkEllipse(Size size,RotatedRect rot){
        int thre1 = (((int)rot.center.x /50) -6)*1000;
        int thre2 = (((int)rot.center.x / 50) - 6) * 5000;
        Log.d(TAG, "area=" + String.valueOf(size.area()) +
                   "\ncenter=" + String.valueOf(rot.center)  +
                   "\nthre" + String.valueOf(thre1) + String.valueOf(thre2) +
                   "\nxy" + String.valueOf(size.width) + " " + String.valueOf(size.height) +
                   "\nangle" + String.valueOf(rot.angle));
//        return (size.height - size.width>100 && size.height - size.width<200 && size.area()<20000);
        return ((rot.angle <= 10 || (rot.angle <=180 && rot.angle >=170)) && size.height>size.width);
    }
}

