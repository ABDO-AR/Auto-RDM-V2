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
            if (event != FileObserver.DELETE && s.length() > 4) {
                // Debugging:
                Log.d(TAG, "onEvent-S-Create: " + s);
                // StartOperations:
                model.startMediaOperations();
            }
            //else if (event == FileObserver.DELETE && s.length() > 4) {
            //    // Debugging:
            //    Log.d(TAG, "onEvent-S-Delete: " + s);
            //    // Checking:
            //    if (path.equals(ARAccess.WHATSAPP_IMAGES_PATH)) {
            //        // Showing:
            //        showNotification(R.string.channel_images_description, CHANNEL_IMAGES_ID);
            //        // Start deleting operation from preferences:
            //        startDeletingOperation(s, ARPreferencesManager.IMAGE_COPIED_FILES);
            //    } else if (path.equals(ARAccess.WHATSAPP_VIDEOS_PATH)) {
            //        // Showing:
            //        showNotification(R.string.channel_videos_description, CHANNEL_VIDEOS_ID);
            //        // Start deleting operation from preferences:
            //        startDeletingOperation(s, ARPreferencesManager.VIDEO_COPIED_FILES);
            //    } else if (path.equals(ARAccess.WHATSAPP_DOCUMENTS_PATH)) {
            //        // Showing:
            //        showNotification(R.string.channel_documents_description, CHANNEL_DOCUMENTS_ID);
            //        // Start deleting operation from preferences:
            //        startDeletingOperation(s, ARPreferencesManager.DOCUMENTS_COPIED_FILES);
            //    } else showNotification(R.string.channel_voices_description, CHANNEL_VOICES_ID);
            //}
        }
        // Checking:
        //i(RemoveMe)f (!path.equals(ARAccess.WHATSAPP_VOICES_PATH)) {
        //} else {
        //    // Checking:
        //    if (tempVoices == 0) {
        //        // Debugging:
        //        Log.d(TAG, "onEventCreate: " + s);
        //        // StartOperations:
        //        model.startMediaOperations();
        //        // Increment:
        //        tempVoices++;
        //    }
        //}
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
