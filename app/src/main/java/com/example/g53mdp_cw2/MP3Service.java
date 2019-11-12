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
import androidx.core.app.TaskStackBuilder;

public class MP3Service extends Service {

    private final String CHANNEL_ID = "100";
    private final int NOTIFICATION_ID = 001;

    private final IBinder binder = new MP3Binder();
    private final MP3Player mp3Player = new MP3Player();

    @Override
    public IBinder onBind(Intent intent) {

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
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(mainIntent);
        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setSound(null)
                        .setContentTitle("MP3 Player")
                        .setContentText("Back to player")
                        .setContentIntent(mainPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
        //notificationManager.notify(NOTIFICATION_ID, builder.build());
        startForeground(NOTIFICATION_ID, builder.build());

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
