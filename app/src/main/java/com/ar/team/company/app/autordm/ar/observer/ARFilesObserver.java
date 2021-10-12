package com.ar.team.company.app.autordm.ar.observer;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

@SuppressWarnings("unused")
public class ARFilesObserver extends FileObserver {

    // Fields:
    private final Context context;
    private final String path;
    private final HomeViewModel model;
    // Fields(Debugging):
    private static final String TAG = "ARFilesObserver";

    // Constructor:
    public ARFilesObserver(Context context, String path, HomeViewModel model) {
        // Super:
        super(path);
        // Initializing:
        this.context = context;
        this.path = path;
        this.model = model;
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
                // Checking:
                if (path.equals(ARAccess.WHATSAPP_VOICES_PATH)) {
                    // It's voices observer:
                    Log.d(TAG, "onEvent-S-Voices: " + s);
                    // Start the new operations:
                    ARFilesObserver observer = new ARFilesObserver(context, ARAccess.WHATSAPP_VOICES_PATH + "/" + s, model);
                    // Start observing:
                    observer.startWatching();
                }
            }
        }
    }

    // Getters:
    public String getPath() {
        return path;
    }

    public Context getContext() {
        return context;
    }
}
