package com.pickupgame.evans.choose.Activity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.pickupgame.evans.choose.R;
import com.pickupgame.evans.choose.Services.MusicService;
import com.pickupgame.evans.choose.Utilities.var;
import com.plattysoft.leonids.ParticleSystem;

public class StartActivity2 extends musicAppCompatActivity {
    Button mButton, upload;

    //    private MusicService mServ;
//    private boolean mIsBound;
    int soundClick;
    SoundPool soundPool;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        setUpViews();
        startEmitting();
        setButtonListener();
        startMusic();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();
            soundClick = soundPool.load(this, R.raw.footttt, 1);
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        }
        //playMusic();

    }

    public void playSoundWin(int id) {
        soundPool.play(id, 1.0f, 1.0f, 1, 0, 1);
    }


    private void startMusic() {
        //If some other song is already playing, stop the service
        if (var.playing) {
            Intent i = new Intent(StartActivity2.this, MusicService.class);
            stopService(i);
        }

        var.backgroundMusic = R.raw.bensound_hey;
        var.playing = true;

        //Play the selected song by starting the service
        Intent start = new Intent(StartActivity2.this, MusicService.class);
        startService(start);
    }

    private void playMusic() {

        if (var.playing) {
            //If song is playing and user clicks on Stop button
            //Stop the song by calling stopService() and change boolean value
            //text on button should be changed to 'Play'
            var.playing = false;
            Intent i = new Intent(StartActivity2.this, MusicService.class);
            stopService(i);
        } else if (!var.playing) {
            //If song is not playing and user clicks on Play button
            //Start the song by calling startService() and change boolean value
            //text on button should be changed to 'Stop'
            var.playing = true;
            Intent i = new Intent(StartActivity2.this, MusicService.class);
            startService(i);
        }

    }

    @Override
    protected void onPause() {
//
//            if (!var.isTransitionning)
//            mServ.pauseMusic();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

//        if (mServ!=null)
//    mServ.resumeMusic();
    }

    private void setUpViews() {
        mButton = findViewById(R.id.startbutton);
        upload = findViewById(R.id.upload);
    }

    private void startEmitting() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new ParticleSystem(StartActivity2.this, 50, R.drawable.star_pink, 1000, R.id.background)
                        .setSpeedRange(0.1f, 0.25f)
                        .emit(mButton, 100);
            }
        }, 500);

    }

    private void setButtonListener() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StartActivity2.this, CategoryActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                StartActivity2.this.startActivity(myIntent);
                var.isTransitionning = true;
                playSoundWin(soundClick);
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(StartActivity2.this, UploadActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                StartActivity2.this.startActivity(myIntent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        var.isTransitionning = false;
    }
}
