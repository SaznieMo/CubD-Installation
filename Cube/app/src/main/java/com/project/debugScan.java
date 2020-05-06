package com.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


/**
 * Debug class used as a test bench for Computer Vision functionalities. Deprecated after application was built. Only use to enable debugging in future.
 */
public class debugScan extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase mOpenCvCameraView;
    Boolean normal = true;
    Boolean change = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_scan);
        OpenCVLoader.initDebug();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_instructions);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_instructions:
                        break;
                    case R.id.action_solve:
                        startActivity(new Intent(getApplicationContext(),debugScan.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(),Settings.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return true;
            }
        });

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setMaxFrameSize(640, 480);
        mOpenCvCameraView.enableView();
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mOpenCvCameraView.enableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    public void change1(View view) {
        if (normal == false) {
            normal = true;
        } else {
            normal = false;
        }
    }

    public void change2(View view) {
        if (change == false) {
            change = true;
        } else {
            change = false;
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat in = inputFrame.rgba();
        Mat dst1 = new Mat();
        Mat dst2 = new Mat();
        Mat dst3 = new Mat();
        Mat canny = new Mat();
        Mat dst4 = new Mat();
        Mat dst5 = new Mat();
        Imgproc.cvtColor(in,dst5,Imgproc.COLOR_RGBA2RGB);
        ArrayList<Rect> rects = new ArrayList<>();

        Imgproc.cvtColor(in,dst1,Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(dst1,dst2,Imgproc.COLOR_BGR2HSV);
        Imgproc.blur(dst2,dst2,new Size(3,3));
        Core.inRange(dst2,new Scalar(0,0,0), new Scalar(180,255,50),dst3);
        Core.bitwise_not(dst3,dst3);
        Imgproc.Canny(dst3,canny,20,100);
        Imgproc.dilate(canny,dst4, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(9,9)));

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(canny,contours,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        Imgproc.drawContours(dst2,contours,-1,new Scalar(150,0,0),3);

        List<MatOfPoint> hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullList.add(new MatOfPoint(hullPoints));
        }

        contours = hullList;

        if (normal) {
            Imgproc.drawContours(dst5, contours, -1, new Scalar(200, 0, 0), 3);
        } else {
            Imgproc.drawContours(dst5, hullList, -1, new Scalar(200, 0, 0), 3);
        }

        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        Point[] centers = new Point[contours.size()];
        float[][] radius = new float[contours.size()][1];
        Rect largestRect = new Rect(5,5,5,5);

        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            MatOfPoint2f  NewMtx = new MatOfPoint2f(contours.get(i).toArray());
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], Imgproc.arcLength(NewMtx,true)*0.1, true);

            if (contoursPoly[i].total() == 4) {
                Log.d("ok", "" + contoursPoly[i].total());
                boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
                if (boundRect[i].area() > largestRect.area() && Math.abs(1 - (double) boundRect[i].height / boundRect[i].width) <= 0.25 &&
                        Math.abs(1 - (double) boundRect[i].width / boundRect[i].height) <= 0.25){
                    largestRect = new Rect(boundRect[i].x-20,boundRect[i].y-20, boundRect[i].width + 20, boundRect[i].height + 20);
                }
                // Rect toCheck = boundRect[i];
                if (Math.abs(1 - (double) boundRect[i].height / boundRect[i].width) <= 0.3 &&
                        Math.abs(1 - (double) boundRect[i].width / boundRect[i].height) <= 0.3 && boundRect[i].area() > 2000 && boundRect[i].area() <= 6000) {
                    rects.add(boundRect[i]);
                    Log.d("area", "b" + boundRect[i].area());
                }
            }
        }

        for (Rect rect : rects) {
            Imgproc.rectangle(dst5, new Point(rect.tl().x, rect.tl().y), new Point(rect.br().x, rect.br().y), new Scalar(0,200,0), -1);
        }

        return dst5;

    }
}
