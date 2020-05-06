package com.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/*
 *External Libraries used in this class (Full Licenses for all external libraries used available in ReadMe).
 *
 * OpenCV - https://opencv.org - Author: OpenCV team
 *
 * */

/**
 * MainActivity class acts as the entry point to the application.
 * Represents the Scan screen.
 * Implements the core Computer Vision functionality of application.
 */
public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    //Initialise variables to be used.
    private static final String TAG = "OCVSample::Activity";
    private static boolean notFirstTime = false;
    private ImageView[] previews = new ImageView[6];
    private ImageView[] previewStatus = new ImageView[6];
    private boolean[] faceCapturedCheck = new boolean[6];
    private CameraBridgeViewBase mOpenCvCameraView;
    private Button button;
    private TextView textView;
    Mat currentFrame;
    Mat outFrame;
    int facesCaptured = 1;
    String faceConfigString = "";
    String cubeConfigString = "";
    boolean solutionAnimated = Settings.solutionAnimated;
    ArrayList<Scalar> colourThresh;

    /**
     * Default Constructor.
     */
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Initialising of Scan Screen activity.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        OpenCVLoader.initDebug();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Load relevant lighting thresholds.
        if (Settings.isLight){
            setColoursLightEnvironment();
        } else {
            setColoursDarkEnvironment();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_solve);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_instructions:
                        Toast.makeText(MainActivity.this, "Instructions", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Instructions.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_solve:
                        Toast.makeText(MainActivity.this, "Solve", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return true;
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        DisplayMetrics displayMetrics = new DisplayMetrics(); //UNCOMMENT TO ENABLE DEBUGGING.
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int width = displayMetrics.widthPixels;

        //Show dialog box with instructions if first time opening application.
        if (!(notFirstTime)) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Welcome!");
            alertDialog.setMessage("Please point your camera at a Rubik's Cube to begin!");
            alertDialog.setIcon(R.drawable.applogo);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            notFirstTime = true;
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }


        currentFrame = new Mat();
        outFrame = new Mat();
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setMaxFrameSize(640, 480);
        mOpenCvCameraView.enableView();
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        //Initialising previews with default images.
        previews[0] = findViewById(R.id.imageView1);
        previews[0].setImageResource(R.drawable.u);
        previews[0].setRotation(90);
        previews[0].setMaxHeight(50);
        previews[0].setMaxWidth(50);

        previews[1] = findViewById(R.id.imageView2);
        previews[1].setImageResource(R.drawable.r);
        previews[1].setRotation(90);
        previews[1].setMaxHeight(50);
        previews[1].setMaxWidth(50);

        previews[2] = findViewById(R.id.imageView3);
        previews[2].setImageResource(R.drawable.f);
        previews[2].setRotation(90);
        previews[2].setMaxHeight(50);
        previews[2].setMaxWidth(50);

        previews[3] = findViewById(R.id.imageView4);
        previews[3].setImageResource(R.drawable.d);
        previews[3].setRotation(90);
        previews[3].setMaxHeight(50);
        previews[3].setMaxWidth(50);

        previews[4] = findViewById(R.id.imageView5);
        previews[4].setImageResource(R.drawable.l);
        previews[4].setRotation(90);
        previews[4].setMaxHeight(50);
        previews[4].setMaxWidth(50);

        previews[5] = findViewById(R.id.imageView6);
        previews[5].setImageResource(R.drawable.b);
        previews[5].setRotation(90);
        previews[5].setMaxHeight(50);
        previews[5].setMaxWidth(50);

        //Initialising previews status bars.
        previewStatus[0] = findViewById(R.id.imageView7);
        previewStatus[1] = findViewById(R.id.imageView8);
        previewStatus[2] = findViewById(R.id.imageView9);
        previewStatus[3] = findViewById(R.id.imageView10);
        previewStatus[4] = findViewById(R.id.imageView11);
        previewStatus[5] = findViewById(R.id.imageView12);
        previewStatus[0].setVisibility(View.INVISIBLE);
        previewStatus[1].setVisibility(View.INVISIBLE);
        previewStatus[2].setVisibility(View.INVISIBLE);
        previewStatus[3].setVisibility(View.INVISIBLE);
        previewStatus[4].setVisibility(View.INVISIBLE);
        previewStatus[5].setVisibility(View.INVISIBLE);

        button = findViewById(R.id.captureButton);
        textView = findViewById(R.id.textView);

        textView.setVisibility(View.INVISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    /**
     * Called when surface is paused. Disables Camera View.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /**
     * Called when surface resumed. Enables Camera View.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mOpenCvCameraView.enableView();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//        } else {
//            Log.d(TAG, "OpenCV library found inside package. Using it!");
//        }
    }
//
//    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
//        return Collections.singletonList(mOpenCvCameraView);
//    }

    /**
     * Called when surface destroyed. Disables view if CameraView is not null.
     */
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /**
     * Called when surface starts.
     */
    public void onCameraViewStarted(int width, int height) {
    }

    /**
     * Called when surface stops.
     */
    public void onCameraViewStopped() {
    }

    /**
     *
     * @param bgr Blue Green Red values as a Scalar object
     * @return Scalar object containing matched or unmatched HSV values.
     */
    public Scalar colourMatch(Scalar bgr){
        double r = bgr.val[2]/255;
        double g = bgr.val[1]/255;
        double b = bgr.val[0]/255;

        // Code to directly convert from RGB to HSV from: https://www.geeksforgeeks.org/program-change-rgb-color-model-hsv-color-model/
        // h, s, v = hue, saturation, value
        double cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
        double cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
        double diff = cmax - cmin; // diff of cmax and cmin.
        double h = -1;
        double s = -1;

        if (cmax == cmin)
            h = 0;

        else if (cmax == r)
            h = (60 * ((g - b) / diff) + 360) % 360;

        else if (cmax == g)
            h = (60 * ((b - r) / diff) + 120) % 360;

        else if (cmax == b)
            h = (60 * ((r - g) / diff) + 240) % 360;

        if (cmax == 0)
            s = 0;
        else
            s = (diff / cmax) * 100;

        // compute v
        double v = cmax * 100;

        h = h / 2;
        s = (s/100)*255;
        v = (v/100)*255;
        Log.d("RGB:",r + " " + g + " " + b + ")");
        Log.d("HSV",h + " " + s + " " + v + ")");

        //YELLOW
        if (h >= colourThresh.get(0).val[0] && s >= colourThresh.get(0).val[1] && v >= colourThresh.get(0).val[2] && h <= colourThresh.get(1).val[0] && s <= colourThresh.get(1).val[1] && v <= colourThresh.get(1).val[2]) {
            return new Scalar(255, 255, 0,5);
        }

        //BLUE
        if (h >= colourThresh.get(2).val[0] && s >= colourThresh.get(2).val[1] && v >= colourThresh.get(2).val[2] && h <= colourThresh.get(3).val[0] && s <= colourThresh.get(3).val[1] && v <= colourThresh.get(3).val[2]) {
            return new Scalar(0, 180, 255,1);
        }

        //Orange
        if (h >= colourThresh.get(4).val[0] && s >= colourThresh.get(4).val[1] && v >= colourThresh.get(4).val[2] && h <= colourThresh.get(5).val[0] && s <= colourThresh.get(5).val[1] && v <= colourThresh.get(5).val[2]) {
            return new Scalar(255, 150, 0,4);
        }

        //Green
        if (h >= colourThresh.get(6).val[0] && s >= colourThresh.get(6).val[1] && v >= colourThresh.get(6).val[2] && h <= colourThresh.get(7).val[0] && s <= colourThresh.get(7).val[1] && v <= colourThresh.get(7).val[2]) {
            return new Scalar(60, 255, 0, 3);
        }

        //White
        if (s >= colourThresh.get(8).val[1] && v >= colourThresh.get(8).val[2] && s <= colourThresh.get(9).val[1] && v <= colourThresh.get(9).val[2]) {
            return new Scalar(255, 255, 255,0);
        }

        //RED L
        if (h >= colourThresh.get(10).val[0] && s >= colourThresh.get(10).val[1] && v >= colourThresh.get(10).val[2] && h <= colourThresh.get(11).val[0] && s <= colourThresh.get(11).val[1] && v <= colourThresh.get(11).val[2]) {
            return new Scalar(255, 0, 0,2);
        }

        //RED H
        if (h >= colourThresh.get(12).val[0] && s >= colourThresh.get(12).val[1] && v >= colourThresh.get(12).val[2] && h <= colourThresh.get(13).val[0] && s <= colourThresh.get(13).val[1] && v <= colourThresh.get(13).val[2]) {
            return new Scalar(255, 0, 0,2);
        }

        //System.out.println("(" + h + " " + s + " " + v + ")");

            //Return Purple Error code colour
            return new Scalar(255,0,255,6);
        //return new Scalar(0,0,0);
    }

    /**
     * Method to boolean check whether 2 rectangles intersect eachother.
     *
     * @param r1 First Rectangle.
     * @param r2 Second Rectangle.
     * @return Boolean check if r1 and r2 intersect.
     */
    public boolean rectIntersection(Rect r1, Rect r2){

        int top = Math.max(r1.y,r2.y);
        int bottom = Math.min(r1.y+r1.height,r2.y+r2.height);
        int left = Math.max(r1.x,r2.x);
        int right = Math.min(r1.x+r1.width,r2.x+r2.width);

        if(right-left>0 && bottom-top>0){
            return true;
        } else
            return false;


        /* Debug Usage:
        if (r1.br().x > r2.tl().x && r2.br().x > r1.tl().x && r1.br().y > r2.tl().y && r2.br().y > r1.tl().y) {
            Log.d("coord","duplicate found");
            return true;
        } else
            Log.d("coord","No duplicate found");
        return false;*/

    }

    /**
     * Core method for Computer Vision functionality. Called every screen refresh. Detects Rubik's Cube and colours. Outputs results in real time.
     * Utilises OpenCV Library functions. The application of these functions were my own but the actual IP of the functions themselves belong to OpenCV.
     *
     * @param inputFrame Camera View input
     * @return Manipulated matrix
     */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat newFrame = inputFrame.rgba();
        Mat dst1 = new Mat();
        Mat dst2 = new Mat();
        Mat dst3 = new Mat();
        Mat dst4 = new Mat(); //Debug use
        Mat canny = new Mat();
        Mat drawing = newFrame.clone();
        Scalar color = new Scalar(255,0,0);
        ArrayList<Rect> rects = new ArrayList<>();

        /* Debug Use
        Point tl = new Point(in.width()/2 - bound3Rect.width/2,in.height()/2 - bound3Rect.height/2); //CENTER OF MATRIX
        Point br = new Point(in.width()/2 + bound3Rect.width/2,in.height()/2 + bound3Rect.height/2); //CENTER OF MATRIX*/


        //Convert input frame first to BGR, then HSV.
        Imgproc.cvtColor(newFrame,dst1,Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(dst1,dst2,Imgproc.COLOR_BGR2HSV);

        //Blur image and retrieve only black sements.
        Imgproc.blur(dst2,dst2,new Size(3,3));
        Core.inRange(dst2,new Scalar(0,0,0), new Scalar(180,255,50),dst3);
        Core.bitwise_not(dst3,dst3);

        //Apply Canny Edge Detection on inverted black segments, dilate results, and find all contours in matrix.
        Imgproc.Canny(dst3,canny,20,100);
        Imgproc.dilate(canny,canny, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(9,9)));
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(canny,contours,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);

        //Create Convex Hulls for each contour to accommodate concave stickers on Rubik's Cube.
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

        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        Point[] centers = new Point[contours.size()];
        float[][] radius = new float[contours.size()][1];
        Rect largestRect = new Rect(5,5,5,5);

        //For each contour, approximate a polygon. If the polygon has 4 sides, and has properties of a square, add it to rects ArrayList. Also keep track of largest square found.
        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            MatOfPoint2f  NewMtx = new MatOfPoint2f(contours.get(i).toArray());
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], Imgproc.arcLength(NewMtx,true)*0.1, true);

            if (contoursPoly[i].total() == 4) {
                boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
                if (boundRect[i].area() > largestRect.area() && Math.abs(1 - (double) boundRect[i].height / boundRect[i].width) <= 0.25 &&
                        Math.abs(1 - (double) boundRect[i].width / boundRect[i].height) <= 0.25){
                    largestRect = new Rect(boundRect[i].x-20,boundRect[i].y-20, boundRect[i].width + 20, boundRect[i].height + 20);
                }
               // Rect toCheck = boundRect[i];
                if (Math.abs(1 - (double) boundRect[i].height / boundRect[i].width) <= 0.3 &&
                        Math.abs(1 - (double) boundRect[i].width / boundRect[i].height) <= 0.3 && boundRect[i].area() > 2000 && boundRect[i].area() <= 6000) {
                    rects.add(boundRect[i]);
                    Log.d("area", "" + boundRect[i].area());
                }
            }
        }

            //If found facelets are inside largest square (cube outline) and there are no duplicate facelets, then add square to validSquares Arraylist.
            boolean insideCube = false;
            ArrayList<Rect> validSquares = new ArrayList<>();
            //Log.d("debug", Arrays.toString(rects.toArray()));
            boolean nodup = false;
                for (int i = 0; i < rects.size(); i++) {
                    for (int j = 0; j < rects.size(); j++) {

                        if (j != i) {
                            if (!(rectIntersection(rects.get(i), rects.get(j)))) {
                                nodup = true;
                            }
                        }

                    }
                    if ((rectIntersection(largestRect,rects.get(i)))) {
                        insideCube = true;
                    }
                    if (nodup && insideCube){
                        validSquares.add(rects.get(i));
                    }
                    nodup = false;
                    insideCube = false;
                }

            //If there are 9 valid squares, average the colours in each facelet, and display the detected colour on screen.
            if (validSquares.size() <= 9) {
                for (Rect rect : validSquares) {
                    Scalar rgb1 = Core.mean(new Mat(dst1, rect));
                    Log.d("colours", rgb1.val[0] + " " + rgb1.val[1] + " " + rgb1.val[2]);
                    double[] rgb = newFrame.get(rect.y + rect.height / 2, rect.x + rect.width / 2);
                    Log.d("areas", "" + rect.area());
                    try {
                        Log.d("colours", "" + rgb[0] + rgb[1] + rgb[2]);
                        Scalar colourMatched = colourMatch(rgb1);
                        rect.setColour( (int) colourMatched.val[3]);
                        if (!(colourMatched.val[3] == 6)) {
                            Imgproc.rectangle(drawing, new Point(rect.tl().x, rect.tl().y), new Point(rect.br().x, rect.br().y), new Scalar(colourMatched.val[0], colourMatched.val[1], colourMatched.val[2]), 3);
                        } else {
                            Imgproc.rectangle(drawing, new Point(rect.tl().x, rect.tl().y), new Point(rect.br().x, rect.br().y), new Scalar(colourMatched.val[0], colourMatched.val[1], colourMatched.val[2]), -1);
                        }
                    } catch (Exception e) {
                        Log.d("", e.toString());
                        Imgproc.rectangle(drawing, rect.tl(), rect.br(), new Scalar(200, 0, 0), -1);
                    }
                }
            }

            //Draw cube outline on screen
            Imgproc.rectangle(drawing,largestRect.tl(),largestRect.br(), new Scalar(172, 191, 120),2);
//
//        for (Rect rect : rects) {
//            double tlx = rect.tl().x + 10;
//            double tly = rect.tl().y + 10;
//            double brx = rect.br().x - 10;
//            double bry = rect.br().y - 10;
//           // byte buff[] = new byte[(int) drawing.total() * drawing.channels()];
//            double[] rgb = newFrame.get(rect.y + rect.height/2, rect.x +rect.width/2);
//
//            try {
//                Log.d("colours", "" + rgb[0] + rgb[1] + rgb[2]);
//                Imgproc.rectangle(drawing, new Point(rect.tl().x - 75, rect.tl().y - 75), new Point(rect.br().x - 75, rect.br().y - 75), new Scalar(rgb[0], rgb[1], rgb[2]), 2);
//            } catch(Exception e) {
//                Log.d("", e.toString());
//                Imgproc.rectangle(drawing, rect.tl(), rect.br(), new Scalar(200,0,0), 2);
//            }
//        } //WORKING RECTANGLE CODE
//
//        List<Rect> tempRects = Arrays.asList(boundRect);
//        for (int i = 0; i < tempRects.size(); i++) {
//            for (int j = i + 1; j < tempRects.size(); j++) {
//                if (rectIntersection(tempRects.get(i), tempRects.get(j))) {
//                    rects.remove(tempRects.get(j));
//                }
//            }
//        }

        //Debug Usage
        List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);
        for (MatOfPoint2f poly : contoursPoly) {
                if (poly.total() == 4) {
                    contoursPolyList.add(new MatOfPoint(poly.toArray()));
                }
        }
        Imgproc.drawContours(canny, contoursPolyList, -1, color);


        //If there are 9 validSquares, save camera view to display in preview, and add each face colour to configuration string.
        if (validSquares.size() == 9) {
            Log.d("SAVED", "YES");
            Mat toShow = new Mat(drawing,new Rect(largestRect.x+20,largestRect.y+20,largestRect.width-20,largestRect.height-20));
            save(toShow);
            ArrayList<Rect> sorted = sortIntoGrid(validSquares);
            for (Rect rect : sorted) {
                faceConfigString += rect.getColour();


/*              DEBUG USAGE:
                Mat outRect = new Mat(dst1,rect);
//                Scalar rgb1 = Core.mean(new Mat(dst1,rect));
//                Log.d("colors", rgb1.val[0] + " " + rgb1.val[1] + " " + rgb1.val[2]);
                // Imgproc.cvtColor(outRect, outRect, Imgproc.COLOR_RGB2BGR);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"squares");
                //Log.d("hello", file.getAbsolutePath());

                if(!file.exists()){
                    file.mkdir();
                }

                File toFile = new File(file, "" + rand + ".jpg");
                //Log.d("file:",toFile.getPath());
                //Imgproc.cvtColor(processFrame, processFrame, Imgproc.COLOR_RGB2BGR);
                Imgcodecs.imwrite(toFile.getPath(), outRect);
                //Log.d("RECTS", rect.tl() + "," + rect.br());
                rand++;*/
            }

            //Update faceCapturedCheck array, indicating relevant preview to enable.
            faceCapturedCheck[facesCaptured-1] = true;

            //Show detected face in preview, if face is 5th face to be captured, change Capture Button to "Solve" for next face.
            runOnUiThread(new Runnable() {
                public void run() {
                    if (facesCaptured < 6) {
                        previews[facesCaptured - 1].setImageURI(null);
                        previews[facesCaptured - 1].setImageURI(Uri.parse("/data/user/0/com.project/files/mydir/Face" + facesCaptured + ".jpg"));
                        textView.setText("Face:" + faceConfigString);
                    } else if (facesCaptured < 7){
                        previews[facesCaptured - 1].setImageURI(null);
                        previews[facesCaptured - 1].setImageURI(Uri.parse("/data/user/0/com.project/files/mydir/Face" + facesCaptured + ".jpg"));
                        textView.setText("Face:" + faceConfigString);
                        button.setText("Solve!");
                    }
                }
            });
        }

        Log.d("rect", faceConfigString);

        //Rect abcde = new Rect(new Point(150,150),new Point(150,200));
        //Imgproc.rectangle(drawing,abcde.br(),new Point(abcde.tl().x,abcde.y + abcde.height + abcde.height),new Scalar(200,100,100), 5);
        //Log.d("coord",abcde.x + " s s " + abcde.y + " s s " + abcde.tl() + " s s " + abcde.br() + " s s " + abcde.width + " s s " + abcde.height);

        //Return real time detected squares and face to camera view.
        return drawing;
    }

    /**
     * Method called when 9 facelets have been detected. Saved images are later displayed in previews.
     * @param mat Matrix of image to be saved.
     */
    public void save(Mat mat){
        outFrame = mat;
        faceConfigString = "";
        File file = new File(getFilesDir(),"mydir");
        if(!file.exists()){
            file.mkdir();
        }
        File toFile = new File(file, "Face" + facesCaptured + ".jpg");
        Imgproc.cvtColor(outFrame, outFrame, Imgproc.COLOR_RGB2BGR);
        Imgcodecs.imwrite(toFile.getPath(), outFrame);
    }

    /**
     * Reset method called when Reset button is clicked. Re-initialises all variables used to restart scanning procedure.
     * @param view Reset Button
     */
    public void reset(View view){
        facesCaptured = 1;
        faceConfigString = "";
        cubeConfigString = "";

        previews[0].setImageResource(R.drawable.u);
        previews[1].setImageResource(R.drawable.r);
        previews[2].setImageResource(R.drawable.f);
        previews[3].setImageResource(R.drawable.d);
        previews[4].setImageResource(R.drawable.l);
        previews[5].setImageResource(R.drawable.b);
        previewStatus[0].setVisibility(View.INVISIBLE);
        previewStatus[1].setVisibility(View.INVISIBLE);
        previewStatus[2].setVisibility(View.INVISIBLE);
        previewStatus[3].setVisibility(View.INVISIBLE);
        previewStatus[4].setVisibility(View.INVISIBLE);
        previewStatus[5].setVisibility(View.INVISIBLE);
        //Placeholder code for resetting - > reset all variables, Call when error in detection. SAZNIE BE VERY CAREFUL WITH MEMORY DELETION HERE!
    }


    /**
     * Method to utilise light colour thresholds for detecting colours. Called in onCreate() method.
     */
    public void setColoursLightEnvironment(){
        colourThresh = new ArrayList<Scalar>();

        colourThresh.add(new Scalar(25,128,128)); //Yellow L
        colourThresh.add(new Scalar(38,255,255)); //Yellow U

        colourThresh.add(new Scalar(90,50,50)); //Blue L
        colourThresh.add(new Scalar(120,255,255));

        colourThresh.add(new Scalar(3,100,50)); //Orange L
        colourThresh.add(new Scalar(24,255,255));

        colourThresh.add(new Scalar(38,50,50)); //Green
        colourThresh.add(new Scalar(80,255,255));

        colourThresh.add(new Scalar(0,0,110)); //WHITE
        colourThresh.add(new Scalar(180,130,255));

        colourThresh.add(new Scalar(0, 0, 0)); //Red L
        colourThresh.add(new Scalar(2.999999999, 255, 255));

        colourThresh.add(new Scalar(170, 70, 50)); //Red2 L
        colourThresh.add(new Scalar(180, 255, 255));
    }

    /**
     * Method to utilise dark colour thresholds for detecting colours. Called in onCreate() method.
     */
    public void setColoursDarkEnvironment(){
        colourThresh = new ArrayList<Scalar>();

        colourThresh.add(new Scalar(25,128,128)); //Yellow L
        colourThresh.add(new Scalar(38,255,255)); //Yellow U

        colourThresh.add(new Scalar(90,50,30)); //Blue L
        colourThresh.add(new Scalar(120,255,255));

        colourThresh.add(new Scalar(5,100,50)); //Orange L
        colourThresh.add(new Scalar(24,255,255));

        colourThresh.add(new Scalar(38,50,50)); //Green
        colourThresh.add(new Scalar(80,255,255));

        colourThresh.add(new Scalar(0,0,110)); //WHITE
        colourThresh.add(new Scalar(180,130,255));

        colourThresh.add(new Scalar(0, 0, 0)); //Red L
        colourThresh.add(new Scalar(4.999999, 255, 255));

        colourThresh.add(new Scalar(170, 70, 50)); //Red2 L
        colourThresh.add(new Scalar(180, 255, 255));

    }

    /**
     * captureButton method called when Capture/Solve Button is clicked. If on 6th face scan, send configuration to solution functions. Else, capture face and update facesCaptured count.
     * @param view
     */
    public void captureButton(View view) {
        //If faceCaptured < 6, add face configuration to configString, else add configuration  to configString and request solution.
        if (faceCapturedCheck[facesCaptured - 1] == true) {

            runOnUiThread(new Runnable() {
                public void run() {
                    previewStatus[facesCaptured - 1].setVisibility(View.VISIBLE);
                }
            });

            if (facesCaptured < 6) {
                cubeConfigString += faceConfigString;
            } else if (facesCaptured < 7) {
                cubeConfigString += faceConfigString;
                Log.d("len", cubeConfigString + "len" + cubeConfigString.length());
                    textView.setText("Loading Solution..");

                    if (solutionAnimated) {
                        Intent intent = new Intent(this, SolutionAnimated.class);
                        intent.putExtra("cubeState", cubeConfigString);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, Solution.class);
                        intent.putExtra("cubeState", cubeConfigString);
                        startActivity(intent);
                    }
                //}
            }
            facesCaptured++;
        }
    }

    /**
     * Sorting method utilsiing bubble sort to arrange the rectangles from left to right, top to bottom in a 3x3 grid.
     *
     * @param rectsToSort
     * @return Sorted rectangles, left to right, top bottom in a 3x3 grid.
     */
    public ArrayList<Rect> sortIntoGrid(ArrayList<Rect> rectsToSort){
        Rect[] unsorted9 = rectsToSort.toArray(new Rect[rectsToSort.size()]);

        Rect temp;
        //Sort entire List first by Y Values
        for (int i = 0; i < 9; i++) {
            for (int j = i+1; j < 9; j++) {
                if(unsorted9[i].x >= unsorted9[j].x) {
                    temp = unsorted9[i];
                    unsorted9[i] = unsorted9[j];
                    unsorted9[j] = temp;
                }
            }
        }

        Rect temp1;

        //Sort top row by X
        for (int i = 0; i < 3; i++) {
            for (int j = i+1; j < 3; j++) {
                if(unsorted9[i].y < unsorted9[j].y) {
                    temp1 = unsorted9[i];
                    unsorted9[i] = unsorted9[j];
                    unsorted9[j] = temp1;
                }
            }
        }

        Rect temp2;

        //Sort middle row by X
        for (int i = 3; i < 6; i++) {
            for (int j = i+1; j < 6; j++) {
                if(unsorted9[i].y < unsorted9[j].y) {
                    temp2 = unsorted9[i];
                    unsorted9[i] = unsorted9[j];
                    unsorted9[j] = temp2;
                }
            }
        }

        Rect temp3;

        //Sort bottom row by X
        for (int i = 6; i < 9; i++) {
            for (int j = i+1; j < 9; j++) {
                if(unsorted9[i].y < unsorted9[j].y) {
                    temp3 = unsorted9[i];
                    unsorted9[i] = unsorted9[j];
                    unsorted9[j] = temp3;
                }
            }
        }

        //return new sorted list top to bottom left to right
       return new ArrayList<>(Arrays.asList(unsorted9));



    }

    
}
