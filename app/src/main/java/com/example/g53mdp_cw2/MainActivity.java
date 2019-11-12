package com.example.g53mdp_cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MP3Player mp3Player = new MP3Player();

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
                mp3Player.load(selectedFile.getPath());
                mp3Player.play();
            }
        });

        final Button playButton = findViewById(R.id.play_button);
        final Button pauseButton = findViewById(R.id.pause_button);
        final Button stopButton = findViewById(R.id.stop_button);

        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mp3Player.play();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mp3Player.pause();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mp3Player.stop();
            }
        });

    }
}
