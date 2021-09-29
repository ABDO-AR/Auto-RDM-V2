package com.ar.team.company.app.autordm;

import android.app.Application;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.ar.team.company.app.autordm.control.foreground.ARForegroundService;

public class BaseApplication extends Application {

    // TAGS:
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // StartingForegroundService:
        ContextCompat.startForegroundService(this, new Intent(this, ARForegroundService.class));
    }
}
