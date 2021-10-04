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
    // Channels:
    public static final String CHANNEL_IMAGES_ID = "AutoRDMDeletedImagesMessage";
    public static final String CHANNEL_VIDEOS_ID = "AutoRDMDeletedVideosMessage";
    public static final String CHANNEL_VOICES_ID = "AutoRDMDeletedVoicesMessage";
    public static final String CHANNEL_DOCUMENTS_ID = "AutoRDMDeletedDocumentsMessage";
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
            } else if (event == FileObserver.DELETE && s.length() > 4) {
                // Debugging:
                Log.d(TAG, "onEvent-S-Delete: " + s);
                // Checking:
                if (path.equals(ARAccess.WHATSAPP_IMAGES_PATH)) {
                    // Showing:
                    showNotification(R.string.channel_images_description, CHANNEL_IMAGES_ID);
                    // Start deleting operation from preferences:
                    startDeletingOperation(s, ARPreferencesManager.IMAGE_COPIED_FILES);
                } else if (path.equals(ARAccess.WHATSAPP_VIDEOS_PATH)) {
                    // Showing:
                    showNotification(R.string.channel_videos_description, CHANNEL_VIDEOS_ID);
                    // Start deleting operation from preferences:
                    startDeletingOperation(s, ARPreferencesManager.VIDEO_COPIED_FILES);
                } else if (path.equals(ARAccess.WHATSAPP_DOCUMENTS_PATH)) {
                    // Showing:
                    showNotification(R.string.channel_documents_description, CHANNEL_DOCUMENTS_ID);
                    // Start deleting operation from preferences:
                    startDeletingOperation(s, ARPreferencesManager.DOCUMENTS_COPIED_FILES);
                } else showNotification(R.string.channel_voices_description, CHANNEL_VOICES_ID);
            }
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

    private void startDeletingOperation(String s, String key) {
        String whatsAppFiles = manager.getStringPreferences(key);
        // Checking:
        if (whatsAppFiles.contains(s)) {
            // Splitting:
            String[] filesNames = whatsAppFiles.split(",");
            StringBuilder builder = new StringBuilder();
            // Looping:
            for (String name : filesNames) {
                // Checking:
                if (!name.equals(s)) {
                    // Adding:
                    builder.append(name).append(",");
                }
            }
            // Setting new preferences:
            manager.setStringPreferences(key, builder.toString());
        }
    }

    // Method(ShowNotification)
    private void showNotification(@StringRes int channelDes, String id) {
        // Creating:
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        createNotificationChannel(notificationManager, channelDes, id);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.drawable.ic_notification_small_icon)
                .setContentTitle(context.getString(R.string.channel_name))
                .setContentText(context.getString(channelDes))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(881231, builder.build());
    }

    // Method(Notification):
    private void createNotificationChannel(NotificationManager notificationManager, @StringRes int channelDes, String id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(channelDes);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
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
