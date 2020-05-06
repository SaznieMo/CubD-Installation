package com.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.animcubeandroid.AnimCube;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.min2phase.Search;



/*
*External Libraries used in this project (Full Licenses available in ReadMe).
*
* Min2Phase - https://github.com/cs0x7f/min2phase - Author: Chen Shuang
* AnimCubeAndroid - https://github.com/cjurjiu/AnimCubeAndroid - Author: Catalin Jurjiu
*
* */


/**
 * SolutionAnimated class for implementing the animated 3D cube solution view.
 */
public class SolutionAnimated extends AppCompatActivity {

    String inputCube = "";
    Search search = new Search();
    AnimCube animCube; //Utilises AnimCubeAndroid library
    int currentMoveCount = 0;
    int maxMoves;
    Button nextButton;
    Button backButton;
    double timeTakenToSolve = 0;

    /**
     * Method called when activity is first created. Initialises UI and implements solving functionality.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_animated);

        //Initialising of Bottom Navigation Bar.
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_solve);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_instructions:
                        //Toast.makeText(MainActivity.this, "Instructions", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Instructions.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_solve:
                        //Toast.makeText(getApplicationContext().this, "Solve", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_settings:
                        // Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return true;
            }
        });

        TextView textView = findViewById(R.id.solutionText);

        animCube = findViewById(R.id.animcube); //Utilises AnimCubeAndroid library
        animCube.setVisibility(View.INVISIBLE); //Utilises AnimCubeAndroid library

        nextButton = findViewById(R.id.nextMove);
        backButton = findViewById(R.id.backMove);

        backButton.setAlpha(0.5f);
        backButton.setClickable(false);

        RelativeLayout rellayout = findViewById((R.id.rellayout));

        // Get the Intent that started this activity and extract the string to cubeState.
        Intent intent = getIntent();
        String cubeState = intent.getStringExtra("cubeState");

        /* Conversion from 1 -> 2 order of faces.
         U D F B L R (2)
         U R F D L B (1)*/
        String cubeAnimatedFormat = cubeState.substring(0,9) + cubeState.substring(27,36) + cubeState.substring(18,27) + cubeState.substring(45,54) + cubeState.substring(36,45) + cubeState.substring(9,18);


        //The following converts all faces into their correct formats where necessary to match the facelet formation of the 3D animation library functions.

        //Up face Correct Format
        String upFsplit = cubeState.substring(0,9);
        String upFaceAnim = upFsplit.substring(6,9) + upFsplit.substring(3,6) + upFsplit.substring(0,3);

        //Down face Correct Format
        String[] downFsplit = cubeState.substring(27,36).split("");
        String temp = "";

        temp = downFsplit[2];
        downFsplit[2] = downFsplit[4];
        downFsplit[4] = temp;

        temp = downFsplit[3];
        downFsplit[3] = downFsplit[7];
        downFsplit[7] = temp;

        temp = downFsplit[6];
        downFsplit[6] = downFsplit[8];
        downFsplit[8] = temp;
        
        String downFaceAnim = downFsplit[1] + downFsplit[2] + downFsplit[3] + downFsplit[4] + downFsplit[5] + downFsplit[6] + downFsplit[7] + downFsplit[8] + downFsplit[9];

        //front face Correct Format
        String[] frontFsplit = cubeState.substring(18,27).split("");
        temp = "";

        temp = frontFsplit[2];
        frontFsplit[2] = frontFsplit[4];
        frontFsplit[4] = temp;

        temp = frontFsplit[3];
        frontFsplit[3] = frontFsplit[7];
        frontFsplit[7] = temp;

        temp = frontFsplit[6];
        frontFsplit[6] = frontFsplit[8];
        frontFsplit[8] = temp;

        String frontFaceAnim = frontFsplit[1] + frontFsplit[2] + frontFsplit[3] + frontFsplit[4] + frontFsplit[5] + frontFsplit[6] + frontFsplit[7] + frontFsplit[8] + frontFsplit[9];


        //back face Correct Format
        String[] backFsplit = cubeState.substring(45,54).split("");
        temp = "";

        temp = backFsplit[2];
        backFsplit[2] = backFsplit[4];
        backFsplit[4] = temp;

        temp = backFsplit[3];
        backFsplit[3] = backFsplit[7];
        backFsplit[7] = temp;

        temp = backFsplit[6];
        backFsplit[6] = backFsplit[8];
        backFsplit[8] = temp;

        String backFaceAnim = backFsplit[1] + backFsplit[2] + backFsplit[3] + backFsplit[4] + backFsplit[5] + backFsplit[6] + backFsplit[7] + backFsplit[8] + backFsplit[9];
        
        
        //left face Correct Format
        String[] leftFsplit = cubeState.substring(36,45).split("");
        temp = "";
        
        temp = leftFsplit[1];
        leftFsplit[1] = leftFsplit[3];
        leftFsplit[3] = temp;

        temp = leftFsplit[4];
        leftFsplit[4] = leftFsplit[6];
        leftFsplit[6] = temp;

        temp = leftFsplit[7];
        leftFsplit[7] = leftFsplit[9];
        leftFsplit[9] = temp;

        String leftFaceAnim = leftFsplit[1] + leftFsplit[2] + leftFsplit[3] + leftFsplit[4] + leftFsplit[5] + leftFsplit[6] + leftFsplit[7] + leftFsplit[8] + leftFsplit[9];



        //right face Correct Format
        String[] rightFsplit = cubeState.substring(9,18).split("");
        temp = "";

        temp = rightFsplit[2];
        rightFsplit[2] = rightFsplit[4];
        rightFsplit[4] = temp;

        temp = rightFsplit[3];
        rightFsplit[3] = rightFsplit[7];
        rightFsplit[7] = temp;

        temp = rightFsplit[6];
        rightFsplit[6] = rightFsplit[8];
        rightFsplit[8] = temp;

        String rightFaceAnim = rightFsplit[1] + rightFsplit[2] + rightFsplit[3] + rightFsplit[4] + rightFsplit[5] + rightFsplit[6] + rightFsplit[7] + rightFsplit[8] + rightFsplit[9];

        cubeAnimatedFormat = upFaceAnim + downFaceAnim + frontFaceAnim + backFaceAnim + leftFaceAnim + rightFaceAnim;

        Log.d("anim5", upFaceAnim);
        Log.d("anim5", upFaceAnim);
        Log.d("anim", "" + cubeAnimatedFormat.length());
        Log.d("anim", cubeAnimatedFormat);

        //Debug usage:
        //String[] faces = cubeAnimatedFormat.split(".");

        //Add configuration to inputCube to be used for
        for (int i = 0; i < 54; i++) {
            Log.d("anim1", inputCube);
            Log.d("anim2", cubeAnimatedFormat);
            inputCube += getColour(cubeAnimatedFormat.charAt(i));
        }

        long startTime = System.nanoTime();
        Search.init(); //utilises Min2Phase Library
        Log.d("lengtj ", "s" + (System.nanoTime() - startTime) / 1.0E6 + " ms");

        //Converting colours to relative positions to match input format of solving library.
        char uFace = cubeState.charAt(4);
        char rFace = cubeState.charAt(13);
        char fFace = cubeState.charAt(22);
        char dFace = cubeState.charAt(31);
        char lFace = cubeState.charAt(40);
        char bFace = cubeState.charAt(49);

        Log.d("cube",cubeState);

        StringBuffer s = new StringBuffer(54);

        for (int i = 0; i < 54; i++)
            s.insert(i, 'A');// default initialization

        for (int i = 0; i < 54; i++) {
            if (cubeState.charAt(i) == uFace)
                s.setCharAt(i, 'U');
            if (cubeState.charAt(i) == rFace)
                s.setCharAt(i, 'R');
            if (cubeState.charAt(i) == fFace)
                s.setCharAt(i, 'F');
            if (cubeState.charAt(i) == dFace)
                s.setCharAt(i, 'D');
            if (cubeState.charAt(i) == lFace)
                s.setCharAt(i, 'L');
            if (cubeState.charAt(i) == bFace)
                s.setCharAt(i, 'B');
        }


        //Use library to search for a solution.
        String result = search.solution(s.toString(),21,100,0,0); //utilises Min2Phase Library
        timeTakenToSolve = (System.nanoTime() - startTime) / 1.0E6;
        Log.d("timetaken", timeTakenToSolve + "ms");

        String resultMove= result.trim().replaceAll(" +", " ");
        maxMoves = resultMove.length() - resultMove.replaceAll(" ", "").length() + 1;

        //If solution contains an error, display error. Else, initialise and enable 3D cube.
        if (result.contains("Error")) { //utilises Min2Phase Library
            switch (result.charAt(result.length() - 1)) {
                case '1':
                    result = "Error: There are not exactly nine facelets of each color! \n\n Check the previews to make sure the colours detected are correct.";
                    break;
                case '2':
                    result = "Error: Not all 12 edges exist exactly once! \n\n Are you making sure to scan the cube in the correct order and orientation? For more information check the Instructions tab.";
                    break;
                case '3':
                    result = "Error: One edge has to be flipped! \n\n Are you sure your cube is in a valid state?";
                    break;
                case '4':
                    result = "Error: Not all 8 corners exist exactly once! \n\n Are you making sure to scan the cube in the correct order and orientation? For more information check the Instructions tab.";
                    break;
                case '5':
                    result = "Error: One corner has to be twisted! \n\n Are you sure your cube is in a valid state?";
                    break;
                case '6':
                    result = "Error: Two corners or two edges have to be exchanged! \n\n Are you sure your cube is in a valid state?";
                    break;
                case '7':
                    result = "No solution exists for the given maximum move number! \n\n Your cube could not be solved in less than 20 moves! Contact admin at Saznie.Mohammed@kcl.ac.uk with details.";
                    break;
                case '8':
                    result = "Timeout, no solution found within given maximum time! \n\n Hardware error! Contact admin at Saznie.Mohammed@kcl.ac.uk with details." ;
                    break;
            }
            backButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
            rellayout.setBackgroundResource(0);
            AlertDialog alertDialog = new AlertDialog.Builder(SolutionAnimated.this).create();
            alertDialog.setTitle("Oops!");
            alertDialog.setMessage("There was an error in your scanned cube!");
            alertDialog.setIcon(R.drawable.applogo);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Try Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            textView.setText(result + "\n\nPlease try scanning a valid cube!");
        } else if (result.length() == 0) {
            backButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
            textView.setText("Cube Completed Already! \n Try scanning a scrambled cube!" );
        } else {
            animCube.setVisibility(View.VISIBLE); //Utilises AnimCubeAndroid library
            animCube.initState(inputCube); //Utilises AnimCubeAndroid library
            animCube.setMoveSequence(result); //Utilises AnimCubeAndroid library
            animCube.setDoubleRotationSpeed(Settings.animationSpeed); //Utilises AnimCubeAndroid library
            animCube.setSingleRotationSpeed(Settings.animationSpeed); //Utilises AnimCubeAndroid library

        }

    }

    /**
     * Back method called by back button. Used to go back a step in the solution list.
     * @param view back move button.
     */
    public void backMove(View view){
        Log.d("moveCount", ""+ currentMoveCount);
        Log.d("moveCount", ""+ maxMoves);
        if (currentMoveCount == 1){
            animCube.animateMoveReversed(); //Utilises AnimCubeAndroid library
            //backButton.setVisibility(View.INVISIBLE);XYZ
            backButton.setAlpha(0.5f);
            backButton.setClickable(false);
            currentMoveCount = currentMoveCount - 1;
        } else if (currentMoveCount == maxMoves) {
            animCube.animateMoveReversed(); //Utilises AnimCubeAndroid library
            //nextButton.setVisibility(View.VISIBLE);XYZ
            nextButton.setAlpha(1.0f);
            nextButton.setClickable(true);
            currentMoveCount = currentMoveCount - 1;
        } else if (currentMoveCount > 1) {
            animCube.animateMoveReversed(); //Utilises AnimCubeAndroid library
            currentMoveCount = currentMoveCount - 1;
        }
    }

    /**
     * Next method called by next button. Used to go back a step in the solution list.
     * @param view next move button.
     */
    public void nextMove(View view){
        currentMoveCount++;
        if (currentMoveCount == 1) {
            animCube.animateMove(); //Utilises AnimCubeAndroid library
            backButton.setAlpha(1.0f);
            backButton.setClickable(true);
            //backButton.setVisibility(View.VISIBLE); XYZ
        } else if (currentMoveCount < maxMoves) {
            animCube.animateMove(); //Utilises AnimCubeAndroid library
        } else if (currentMoveCount == maxMoves){
            animCube.animateMove(); //Utilises AnimCubeAndroid library
            Log.d("moveCount", ""+ currentMoveCount);
            Log.d("moveCount", ""+ maxMoves);
            //nextButton.setVisibility(View.INVISIBLE);XYZ
            nextButton.setAlpha(0.5f);
            nextButton.setClickable(false);
        }
    }

    /**
     * Overriding the hardware back button to return back to scan screen.
     */
    @Override
    public void onBackPressed() {
        if (true) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            overridePendingTransition(0,0);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Getter for colour codes given colours as characters.
     *
     * @param colour Character representation of colour.
     * @return Integer value as string to match input of library.
     */
    public String getColour(char colour){

        if (colour == 'W'){
            return "0";
        } else if (colour == 'Y') {
            return "1";
        } else if (colour == 'O') {
            return "2";
        } else if (colour == 'R') {
            return "3";
        } else if (colour == 'B') {
            return "4";
        } else //colour = G
            return "5";
    }
}
