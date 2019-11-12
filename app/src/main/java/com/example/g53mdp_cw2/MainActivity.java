package com.example.g53mdp_cw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "100";
    private final int NOTIFICATION_ID = 001;

    private MP3Service.MP3Binder service = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("MP3 Time", "onServiceConnected");
            service = (MP3Service.MP3Binder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MP3 Time", "onServiceDisconnected");
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        this.bindService(new Intent(this, MP3Service.class),
                serviceConnection, Context.BIND_AUTO_CREATE);


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

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setSound(null)
                        .setContentTitle("MP3 Player")
                        .setContentText("Back to player")
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManager.notify(NOTIFICATION_ID, builder.build());


        final ListView fileListView = findViewById(R.id.file_list);
        String path = Environment.getExternalStorageDirectory().getPath() + "/Music/";
        File musicDir = new File(path);
        Log.d("MP3 Time", path);
        File[] mp3FileList = musicDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getPath().endsWith(".mp3");
            }
        });

        final TextView headerTextView = findViewById(R.id.file_header_text);

        if (mp3FileList == null) {
            headerTextView.setText(R.string.no_access);
        } else if (mp3FileList.length < 1) {
            headerTextView.setText(R.string.no_files);
        } else {
            fileListView.setAdapter(new ArrayAdapter<File>(this,
                    android.R.layout.simple_list_item_1, mp3FileList));
        }


        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File selectedFile = (File) (fileListView.getItemAtPosition(i));
                service.loadMP3(selectedFile.getPath());
                service.playMP3();
            }
        });

        final Button playButton = findViewById(R.id.play_button);
        final Button pauseButton = findViewById(R.id.pause_button);
        final Button stopButton = findViewById(R.id.stop_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.playMP3();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.pauseMP3();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.stopMP3();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MP3 Time", "MainActivity DESTROYED");
    }
}
