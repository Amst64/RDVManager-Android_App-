package com.example.project;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

public class MusicService extends Service{

    MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t= new Thread(new Runnable() {
            @Override
            public void run() {
                player = MediaPlayer.create(getApplicationContext(), R.raw.wii_music);
                player.setLooping(true);
                player.start();
            }
        });
        t.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();}

}
