package com.pickupgame.evans.choose.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.pickupgame.evans.choose.Utilities.var;

public class MusicService extends Service {

    public final IBinder mBinder = new ServiceBinder();
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mediaPlayer = MediaPlayer.create(this, var.backgroundMusic);
            mediaPlayer.setLooping(true); // Set looping
            mediaPlayer.setVolume(100, 100);
        } catch (Exception e) {
            Log.i("show", "Error: " + e.toString());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer.start();

        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
    }

    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            var.length = mediaPlayer.getCurrentPosition();

        }
    }

    public void resumeMusic() {
        if (mediaPlayer.isPlaying() == false) {
            mediaPlayer.seekTo(var.length);
            mediaPlayer.start();
        }
    }

    public void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


}