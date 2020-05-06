package com.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.min2phase.Search;

import java.util.ArrayList;


/*
 *External Libraries used in this project (Full Licenses available in ReadMe).
 *
 * Min2Phase - https://github.com/cs0x7f/min2phase - Author: Chen Shuang
 *
 * */

/**
 * Solution class providing functionality for list view solution screen and hiding of final stages of solution. Called when user clicks Solve Button on scan screen and the corresponding setting in Settings is true.
 */
public class Solution extends AppCompatActivity {

    Search search = new Search();
    String[] moveByMove;
    Object[] imageMovesList;
    ListView listView;
    int mSelectedItem = 0;
    String unveiled = ".";
    int numberToHide = 0;

    /**
     * Method called when Solution activity is loaded. Initialises navigation bar and displays solution as list view.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);
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

        // Get the Intent (passed variable) - cubeConfigString from Scan screen, as well as number of final steps to hide from Settings value.
        numberToHide = Settings.hiddenListNumber;
        Intent intent = getIntent();
        String cubeState = intent.getStringExtra("cubeState");

        long startTime = System.nanoTime();

        //Initialise algorithm search function.
        Search.init(); //utilises Min2Phase Library

        Log.d("TimeStart ", "s" + (System.nanoTime() - startTime) / 1.0E6 + " ms");

        //Get colours in center pieces for universal position conversion
        char uFace = cubeState.charAt(4);
        Log.d("uF", "" + uFace);
        char rFace = cubeState.charAt(13);
        Log.d("rF", "" + rFace);
        char fFace = cubeState.charAt(22);
        Log.d("fF", "" + fFace);
        char dFace = cubeState.charAt(31);
        Log.d("dF", "" + dFace);
        char lFace = cubeState.charAt(40);
        Log.d("lF", "" + lFace);
        char bFace = cubeState.charAt(49);
        Log.d("bF", "" + bFace);

        Log.d("cubeState",cubeState);

        StringBuffer s = new StringBuffer(54);

        for (int i = 0; i < 54; i++)
            s.insert(i, 'A');// default initialization

        //Convert current cubeState string from colours to relative positions.
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

        Log.d("lengthOfCubeConfig", s.toString());

        //FOR DEBUG
        String result =  search.solution(s.toString(),30,100,0,0); //utilises Min2Phase Library
        Log.d("result", result);

        //Split moves into single steps.
        moveByMove = result.split("\\s+");


        ArrayList<Integer> imageMoves = new ArrayList<Integer>(0);

        //Match image array to solution steps so they can be used later as a pair.
        for (int i = 0; i < moveByMove.length; i++) {
            Log.d("result", "h: " + moveByMove[i]);
            switch (moveByMove[i]) {
                case "F":
                    imageMoves.add(R.drawable.fc);
                    break;
                case "F'":
                    imageMoves.add(R.drawable.fac);
                    break;
                case "U":
                    imageMoves.add(R.drawable.uc);
                    break;
                case "U'":
                    imageMoves.add(R.drawable.uac);
                    break;
                case "R":
                    imageMoves.add(R.drawable.rc);
                    break;
                case "R'":
                    imageMoves.add(R.drawable.rac);
                    break;
                case "L":
                    imageMoves.add(R.drawable.lc);
                    break;
                case "L'":
                    imageMoves.add(R.drawable.lac);
                    break;
                case "B":
                    imageMoves.add(R.drawable.bc);
                    break;
                case "B'":
                    imageMoves.add(R.drawable.bac);
                    break;
                case "D":
                    imageMoves.add(R.drawable.dc);
                    break;
                case "D'":
                    imageMoves.add(R.drawable.dac);
                    break;
                case "F2":
                    imageMoves.add(R.drawable.fc);
                    break;
                case "U2":
                    imageMoves.add(R.drawable.uc);
                    break;
                case "L2":
                    imageMoves.add(R.drawable.lc);
                    break;
                case "R2":
                    imageMoves.add(R.drawable.rc);
                    break;
                case "B2":
                    imageMoves.add(R.drawable.bc);
                    break;
                case "D2":
                    imageMoves.add(R.drawable.dc);
                    break;
                default:
                    //Debug Usage
                    imageMoves.add(R.drawable.test1);
            }
        }

        imageMovesList = imageMoves.toArray();

        //Conditional to check what to display: Error message, Cube completed message, or solution steps as a list.
        if (result.contains("Error")) {
            switch (result.charAt(result.length() - 1)) { //utilises Min2Phase Library
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
            //Create alert dialog to inform user error has been made.
            AlertDialog alertDialog = new AlertDialog.Builder(Solution.this).create();
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
            textView.setText("Cube Completed Already! \n Try scanning a scrambled cube!");
        } else {
            //Create list view and enable functionality to unveil a hidden cell.
            listView = findViewById(R.id.listView);
            final CustomAdapter customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    unveiled = unveiled+i+".";
                    mSelectedItem = i;
                    customAdapter.notifyDataSetChanged();

                }
            });
        }

    }


    /**
     * Hard coded back button functionality
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
     * Adapter class to allow for custom cell formed of image and matching solution move.
     */
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return imageMovesList.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        /**
         * If unveiled, show cell, else show "Tap to show" text only.
         *
         * @param i Used to determine what has been unveiled
         * @param convertView
         * @param viewGroup
         * @return
         */
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            View view = getLayoutInflater().inflate(R.layout.solutioncell,null);
            ImageView mImageView = (ImageView) view.findViewById(R.id.cellImage);
            TextView mTextView = (TextView) view.findViewById(R.id.cellText);

            if (unveiled.contains("." + i + ".")) {
                mImageView.setImageResource((int) imageMovesList[i]);
                mTextView.setText(moveByMove[i]);
            } else if (!(moveByMove.length - i <= numberToHide)) {
                mImageView.setImageResource((int) imageMovesList[i]);
                mTextView.setText(moveByMove[i]);
            } else {
                mImageView.setImageResource(R.drawable.questionmark);
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                mTextView.setText("Tap to Show!");
            }

            return view;
        }
    }
}
