package com.example.g53mdp_cw2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity {


    private MP3Service.MP3Binder service = null;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("MP3 Time", "onServiceConnected");
            service = (MP3Service.MP3Binder) iBinder;
            service.registerCallback(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MP3 Time", "onServiceDisconnected");
            service.unregisterCallback();
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent serviceIntent = new Intent(this, MP3Service.class);
        this.startService(serviceIntent);
        this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

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
            fileListView.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, mp3FileList));
        }


        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File selectedFile = (File) (fileListView.getItemAtPosition(i));
                service.loadMP3(selectedFile.getPath());
                service.playMP3();
                service.updateTimer();
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

    ICallback callback = new ICallback() {
        @Override
        public void elapsedTimeEvent(final int duration, final int elapsedTime) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressBar songProgress = findViewById(R.id.song_progress);
                    songProgress.setMax(duration);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        songProgress.setProgress(elapsedTime, true);//animate if SDK > 15
                    } else {
                        songProgress.setProgress(elapsedTime);
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MP3 Time", "MainActivity DESTROYED");
        if(serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }
}
