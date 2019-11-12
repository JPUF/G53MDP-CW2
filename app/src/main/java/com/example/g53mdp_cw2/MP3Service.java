package com.example.g53mdp_cw2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MP3Service extends Service {

    private final IBinder binder = new MP3Binder();
    private final MP3Player mp3Player = new MP3Player();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MP3Binder extends Binder {

        void loadMP3(String filePath) {
            Log.d("MP3 Time", "Loading MP3");
            mp3Player.load(filePath);
        }

        void playMP3() {
            Log.d("MP3 Time", "Playing MP3");
            mp3Player.play();
        }

        void pauseMP3() {
            Log.d("MP3 Time", "Pausing MP3");
            mp3Player.pause();
        }

        void stopMP3() {
            Log.d("MP3 Time", "Stopping MP3");
            mp3Player.stop();
        }

    }
}
