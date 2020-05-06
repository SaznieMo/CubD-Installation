package com.project;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Instructions class containing the contents of the Instructions screen.
 */
public class Instructions extends AppCompatActivity {
    //ImageView imageView;
    VideoView videoView;

    /**
     * Initialising of class and bottom navigation bar. Loads and plays video.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_instructions);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_instructions:
                        Toast.makeText(Instructions.this, "Instructions", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_solve:
                        Toast.makeText(Instructions.this, "Solve", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        //startActivity(new Intent(getApplicationContext(),DebugScan.class)); //UNCOMMENT TO ENABLE DEBUG MODE. Access in application by clicking Instructions then Scan tab.
                        overridePendingTransition(0,0);
                        break;
                    case R.id.action_settings:
                        Toast.makeText(Instructions.this, "Settings", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Settings.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return true;
            }
        });

        /**
         * Initialising of video module in Instructions screen. Looped set to true.
         */
        videoView = (VideoView) findViewById(R.id.simpleVideoView);
        Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.scandemo);
        videoView.setVideoURI(video);
        videoView.seekTo(1);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }



}
