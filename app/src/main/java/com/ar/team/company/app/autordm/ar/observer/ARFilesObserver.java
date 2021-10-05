package com.ar.team.company.app.autordm.ar.observer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

@SuppressWarnings("unused")
public class ARFilesObserver extends FileObserver {

    // Fields:
    private final Context context;
    private final HomeViewModel model;
    private final String path;
    private final ARPreferencesManager manager;
    // TempData:
    private static int tempVoices = 0;
    // Fields(Debugging):
    private static final String TAG = "ARFilesObserver";

    // Constructor:
    public ARFilesObserver(Context context, String path, HomeViewModel model) {
        // Super:
        super(path);
        // Initializing:
        this.model = model;
        this.path = path;
        this.context = context;
        this.manager = new ARPreferencesManager(context);
    }

    // Method(Events):
    @Override
    public void onEvent(int event, @Nullable String s) {
        // Debugging:
        Log.d(TAG, "onEvent: " + s);
        Log.d(TAG, "onEvent: " + event);
        // Checking:
        if (s != null) {
            // Checking:
            //event != FileObserver.DELETE &&
            if (s.length() > 4) {
                // Debugging:
                Log.d(TAG, "onEvent-S-Create: " + s);
                // StartOperations:
                model.startMediaOperations();
            }
        }
    }

    // Methods(Reset):
    public static void resetTempVoices() {
        // Resting:
        tempVoices = 0;
    }

    public Context getContext() {
        return context;
    }
}
