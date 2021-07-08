package com.example.audioplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //initializing variables
    TextView tv1;
    ImageView iv1;
    ImageButton ib1, ib2, ib3, ib4;
    SeekBar sb1;
    MediaPlayer mp;
    Boolean check;
    CountDownTimer ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //creating objects
        tv1 = findViewById(R.id.a1_tv1);
        iv1 = findViewById(R.id.a1_iv1);
        ib1 = findViewById(R.id.a1_ib1);
        ib2 = findViewById(R.id.a1_ib2);
        ib3 = findViewById(R.id.a1_ib3);
        ib4 = findViewById(R.id.a1_ib4);
        sb1 = findViewById(R.id.a1_sb1);
        //setting listener for add button
        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a new intent for getting content from system
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //setting type to audio
                intent.setType("audio/*");
                //starting activity for result
                startActivityForResult(intent, 1);
            }
        });
        //setting listener for play button
        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking if the music is to be played from the beginning
                if(check) {
                    //setting maximum value of seekbar equal to the duration of song
                    sb1.setMax(mp.getDuration());
                    //creating a new countdown timer with interval of 1 second
                    ct = new CountDownTimer(mp.getDuration(), 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //setting progress of bar to current position of song
                            sb1.setProgress(mp.getCurrentPosition());
                        }

                        @Override
                        public void onFinish() {

                        }
                    };
                }
                //starting music
                mp.start();
                //starting countdown timer
                ct.start();
            }
        });
        //setting listener for pause button
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pausing music
                mp.pause();
                //stopping countdown timer
                ct.cancel();
                //setting check to false to indicate that the song should not be played from the beginning
                check = false;
            }
        });
        //setting listener for stop button
        ib4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pausing music
                mp.pause();
                //setting song progress to zero
                mp.seekTo(0);
                //stopping countdown timer
                ct.cancel();
                //setting seekbar progress to zero
                sb1.setProgress(0);
                //setting check to zero indicating the song should be played from start
                check = true;
            }
        });
        //setting listener for seekbar
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //checking if the user changed the progress of seekbar
                if(fromUser) {
                    //setting the progress of song to the user entered value
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //executes after user selects a song from files app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //checking if the request code match with the request code given by us
        if(requestCode == 1) {
            //checking if it returns without any error
            if(resultCode == Activity.RESULT_OK) {
                //checking if music is already being played
                if(mp != null)
                    //stopping any previously playing music
                    mp.stop();
                //getting the song
                Uri music = data.getData();
                //creating a media player object for the selected song
                mp = MediaPlayer.create(MainActivity.this, music);
                //function returning name of song
                String name = getFileName(music);
                //setting name of song on textview
                tv1.setText(name);
                //setting check to true to indicate that the song will be played from beginning
                check = true;
            }
            //this code will be execute when there is some error
            else {
                //a simple toast message informing the user that there is some kind of error
                Toast.makeText(this, "Error!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //function to get the name of song
    public String getFileName(Uri uri) {
        //initializing result to null
        String result = null;
        //checking if uri has some content
        if (uri.getScheme().equals("content")) {
            //creating a cursor to fetch content of uri
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            //trying to get the name of song
            try {
                //checking if cursor is not null and contains columns
                if (cursor != null && cursor.moveToFirst()) {
                    //fetching the name of song
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                //closing the cursor
                cursor.close();
            }
        }
        //checking if the result is still null in case of an error in above statements
        if (result == null) {
            //getting the path of song
            result = uri.getPath();
            //getting the last index of /
            int cut = result.lastIndexOf('/');
            //checking that the last index of / exist
            if (cut != -1) {
                //setting the result equal to the rest of the string after that
                result = result.substring(cut + 1);
            }
        }
        //returning result
        return result;
    }
}