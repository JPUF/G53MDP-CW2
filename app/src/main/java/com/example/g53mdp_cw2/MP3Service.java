package com.example.g53mdp_cw2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MP3Service extends Service {

    private final IBinder binder = new MP3Binder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MP3Binder extends Binder {
        void serviceCall() {
            Log.d("MP3 Time", "Binder service call");
        }
    }
}
