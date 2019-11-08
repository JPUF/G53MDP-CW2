package com.example.g53mdp_cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MP3Player mp3Player = new MP3Player();

        final ListView fileListView = (ListView) findViewById(R.id.file_list);

        String path = Environment.getExternalStorageDirectory().getPath() + "/Music/";
        File musicDir = new File(path);

        Log.d("MP3 Time", path);
        File[] mp3FileList = musicDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getPath().endsWith(".mp3");
            }
        });

        if(mp3FileList == null) {//TODO test and probs improve handling of no files.
            mp3FileList = new File[1];
            mp3FileList[0] = new File("emptyfile");
        }

        fileListView.setAdapter(new ArrayAdapter<File>(this,
                android.R.layout.simple_list_item_1, mp3FileList));

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File selectedFile = (File) (fileListView.getItemAtPosition(i));
                mp3Player.load(selectedFile.getPath());
                mp3Player.play();
            }
        });

    }
}
