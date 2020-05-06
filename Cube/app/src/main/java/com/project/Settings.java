package com.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Settings Class for implementing the settings screen.
 */
public class Settings extends AppCompatActivity {

    //Default values for settings
    public static boolean solutionAnimated = false;
    public static boolean isLight = false;
    public static int hiddenListNumber = 0;
    public static int animationSpeed = 5;
    public static int animationSpeedInput = 1;

    /**
     * Method called when activity is created. Initialises UI.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Creation of bottom navigation bar.
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_instructions:
                        Toast.makeText(Settings.this, "Instructions", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Instructions.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_solve:
                        Toast.makeText(Settings.this, "Solve", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_settings:
                        Toast.makeText(Settings.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        //Editable UI Text and Validation checks.
        final EditText hiddenListInput = (EditText) findViewById(R.id.editHiddenList);
        hiddenListInput.setText(String.valueOf(hiddenListNumber));
        hiddenListInput.clearFocus();
        hiddenListInput.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int temp = 0;
                if (!(editable.toString().equals(""))) {
                    temp = Integer.parseInt(editable.toString());
                }
                if (temp < 0 || temp > 20) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Please enter a number between 0 and 20!");
                    alertDialog.setIcon(R.drawable.applogo);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    hiddenListInput.setText("0");
                    alertDialog.show();
                } else {
                    hiddenListNumber = temp;
                }
            }
        });

        //Editable UI Text and Validation checks.
        final EditText animSpeedInput = (EditText) findViewById(R.id.animspeedinput);
        animSpeedInput.setText(String.valueOf(animationSpeedInput));
        animSpeedInput.clearFocus();
        animSpeedInput.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int temp = 1;
                if (!(editable.toString().equals(""))) {
                    temp = Integer.parseInt(editable.toString());
                }
                if (temp < 1 || temp > 5) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Please enter a number between 1 and 5!");
                    alertDialog.setIcon(R.drawable.applogo);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    animSpeedInput.setText("1");
                    animationSpeedInput = 1;
                    alertDialog.show();
                } else {
                    animationSpeed = (6 - temp) * 5;
                    animationSpeedInput = animationSpeed / 5;
                }
            }
        });

        //Persist settings for toggles when navigating away from settings.
        RadioGroup animatedToggle = (RadioGroup) findViewById(R.id.animatedToggle);
        if (solutionAnimated){
            animatedToggle.check(R.id.isAnimated);
        } else {
            animatedToggle.check(R.id.isList);
        }

        //Persist settings for toggles when navigating away from settings.
        RadioGroup lightToggle = (RadioGroup) findViewById(R.id.lightToggle);
        if (isLight){
            lightToggle.check(R.id.isLight);
        } else {
            lightToggle.check(R.id.isDark);
        }

    }

    /**
     * Method to select a solution view.
     * @param view Radio Button Clicked
     */
    public void onRadioButtonClickedAnim(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.isAnimated:
                if (checked)
                    solutionAnimated = true;
                    // Pirates are the best
                    break;
            case R.id.isList:
                if (checked)
                    solutionAnimated = false;
                    break;
        }
    }

    /**
     * Method to select a lighting condition.
     * @param view Radio Button Clicked
     */
    public void onRadioButtonClickedLight(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.isDark:
                if (checked)
                    isLight = false;
                // Pirates are the best
                break;
            case R.id.isLight:
                if (checked)
                    isLight = true;
                break;
        }
    }

}
