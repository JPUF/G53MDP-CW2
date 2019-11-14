package com.example.g53mdp_cw2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MP3Service extends Service {

    private final IBinder binder = new MP3Binder();
    private final MP3Player mp3Player = new MP3Player();

    @Override
    public IBinder onBind(Intent intent) {
        String CHANNEL_ID = "100";
        int NOTIFICATION_ID = 1;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MP3 Player notification channel";
            String description = "Notification channel for MP3 Player app";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setSound(null)
                        .setContentTitle("MP3 Player")
                        .setContentText("Back to player")
                        .setContentIntent(mainPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_LOW);

        startForeground(NOTIFICATION_ID, builder.build());

        return binder;
    }

    class MP3Binder extends Binder {

        void loadMP3(String filePath) {
            Log.d("MP3 Time", "Loading MP3");
            MP3Player.MP3PlayerState state = mp3Player.getState();
            if (state == MP3Player.MP3PlayerState.PLAYING || state == MP3Player.MP3PlayerState.PAUSED) {
                mp3Player.stop();
            }
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
            stopSelf();
        }

        int getElapsedTime() {
            if(songIsLoaded()){
                return mp3Player.getProgress();
            }
            else {
                return 0;
            }
        }

        int getDuration() {
            if(songIsLoaded()) {
                return mp3Player.getDuration();
            }
            else {
                return 1;
            }
        }

        private boolean songIsLoaded(){
            MP3Player.MP3PlayerState state = mp3Player.getState();
            return state == MP3Player.MP3PlayerState.PLAYING || state == MP3Player.MP3PlayerState.PAUSED;
        }

    }
}
