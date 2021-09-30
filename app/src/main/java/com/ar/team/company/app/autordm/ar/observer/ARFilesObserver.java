package com.ar.team.company.app.autordm.ar.observer;

import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

@SuppressWarnings("unused")
public class ARFilesObserver extends FileObserver {

    // Fields:
    private final HomeViewModel model;
    private final String path;
    // TempData:
    private static int tempVoices = 0;
    // Fields(Debugging):
    private static final String TAG = "ARFilesObserver";

    // Constructor:
    public ARFilesObserver(String path, HomeViewModel model) {
        // Super:
        super(path);
        // Initializing:
        this.model = model;
        this.path = path;
    }

    // Method(Events):
    @Override
    public void onEvent(int event, @Nullable String s) {
        // Debugging:
        Log.d(TAG, "onEvent: " + s);
        // Checking:
        if (!path.equals(ARAccess.WHATSAPP_VOICES_PATH)) {
            // Checking:
            if (event == FileObserver.CREATE || event == FileObserver.ACCESS) {
                // Debugging:
                Log.d(TAG, "onEventCreate: " + s);
                // StartOperations:
                model.startMediaOperations();
            }
        } else {
            // Checking:
            if (tempVoices == 0) {
                // Debugging:
                Log.d(TAG, "onEventCreate: " + s);
                // StartOperations:
                model.startMediaOperations();
                // Increment:
                tempVoices++;
            }
        }
    }

    // Methods(Reset):
    public static void resetTempVoices() {
        // Resting:
        tempVoices = 0;
    }
}
