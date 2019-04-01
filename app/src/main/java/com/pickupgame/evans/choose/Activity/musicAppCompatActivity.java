package com.pickupgame.evans.choose.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.pickupgame.evans.choose.R;
import com.pickupgame.evans.choose.Services.MusicService;
import com.pickupgame.evans.choose.Utilities.var;

public abstract class musicAppCompatActivity extends AppCompatActivity {

    private MusicService mServ;
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            mServ = ((MusicService.ServiceBinder) service).getService();

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            mServ = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startMusic();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void startMusic() {
        //If some other song is already playing, stop the service
        if (var.playing) {
            Intent i = new Intent(this, MusicService.class);
            stopService(i);
        }

        var.backgroundMusic = R.raw.bensound_hey;
        var.playing = true;

        //Play the selected song by starting the service
        Intent start = new Intent(this, MusicService.class);
        startService(start);
    }

    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        doBindService();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

        if (mServ != null)
            mServ.resumeMusic();
    }

    @Override
    protected void onPause() {

        if (!var.isTransitionning)
            mServ.pauseMusic();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        var.isTransitionning = false;
    }
}