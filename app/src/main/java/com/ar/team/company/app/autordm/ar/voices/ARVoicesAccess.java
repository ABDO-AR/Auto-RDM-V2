package com.ar.team.company.app.autordm.ar.voices;

import android.content.Context;
import android.util.Log;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.control.notifications.ARNotificationManager;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "CommentedOutCode"})
public class ARVoicesAccess {

    // Fields:
    private final Context context;

    // Constructor:
    public ARVoicesAccess(Context context) {
        this.context = context;
    }

    // Method(Static):
    public synchronized static List<File> getVoicesWithDirs(Context context) {
        // Control:
        HomeActivity.setVoicesObserver(false);
        // Initializing:
        ARPreferencesManager manager = new ARPreferencesManager(context, ARPreferencesManager.MODE_FILES);
        File voicesDir = ARAccess.getAppDir(context, ARAccess.VOICES_DIR);
        File[] whatsAppVoicesFiles = getVoicesFiles();
        List<File> voices = new ArrayList<>();
        // Initializing(State):
        boolean state1 = Objects.requireNonNull(voicesDir.listFiles()).length != 0;
        // Looping:
        if (state1) {
            // Checking(Fields):
            String whatsapp = manager.getStringPreferences(ARPreferencesManager.VOICE_COPIED_FILES);
            StringBuilder realWhatsApp = new StringBuilder();
            StringBuilder copied = new StringBuilder();
            // If it reached to here that's mean that there are already copied images.
            // Now we will start a simple for loop and checking each file by name:
            for (File copiedFile : Objects.requireNonNull(voicesDir.listFiles())) {
                // Checking:
                if (!copiedFile.isDirectory()) {
                    // Getting all files name:
                    copied.append(copiedFile.getName()).append(",");
                }
            }
            // Checking:
            if (whatsAppVoicesFiles.length != 0) {
                // We will start checking if file contains this new file or not:
                for (File file : whatsAppVoicesFiles) {
                    // AddingReal:
                    realWhatsApp.append(file.getName()).append(",");
                    // Checking:
                    if (!whatsapp.contains(file.getName()) && !copied.toString().contains(file.getName()) && !file.isDirectory()) {
                        // NotifyManager:
                        manager.setStringPreferences(ARPreferencesManager.VOICE_COPIED_FILES, whatsapp + file.getName() + ",");
                        // Here we will start copy operation because that was new file:
                        ARAccess.copy(file, new File(voicesDir.getAbsolutePath() + "/" + file.getName()));
                    }
                }
                // Temp:
                int tempDel = 0;
                // LastChecking:
                for (File copiedFile : Objects.requireNonNull(voicesDir.listFiles())) {
                    // Checking:
                    if (!copiedFile.isDirectory()) {
                        // Adding:
                        if (!realWhatsApp.toString().contains(copiedFile.getName())) {
                            // Initializing:
                            String pref = manager.getStringPreferences(ARPreferencesManager.VOICE_COPIED_FILES);
                            // Adding(RF):
                            voices.add(copiedFile);
                            // Checking:
                            if (pref.contains(copiedFile.getName())) {
                                // Start showing notification:
                                ARNotificationManager.showNotification(context, R.string.channel_voices_description, ARNotificationManager.CHANNEL_VOICES_ID);
                                // Start removing operations:
                                String[] tempPref = pref.split(",");
                                StringBuilder builder = new StringBuilder();
                                // Looping:
                                for (String temp : tempPref) {
                                    // Checking:
                                    if (!temp.equals(copiedFile.getName())) {
                                        // Removing:
                                        builder.append(temp).append(",");
                                    }
                                }
                                // Resetting:
                                manager.setStringPreferences(ARPreferencesManager.VOICE_COPIED_FILES, builder.toString());
                            }
                        }
                    }
                }
            }
            // Debugging:
            Log.d(ARAccess.TAG, "A11-OP: VoicesAccess WhatsApp Files Name :: " + whatsapp);
        } else {
            // Initializing:
            int tempIndex = 0;
            // Checking:
            if (whatsAppVoicesFiles != null) {
                // Checking:
                if (whatsAppVoicesFiles.length != 0) {
                    // Looping:
                    for (File file : whatsAppVoicesFiles) {
                        // NotifyManager:
                        manager.setStringPreferences(ARPreferencesManager.VOICE_COPIED_FILES, manager.getStringPreferences(ARPreferencesManager.VOICE_COPIED_FILES) + file.getName() + ",");
                        // Getting first 3 images:
                        if (tempIndex <= 1) {
                            // Start creating temp dir:
                            ARAccess.createTempDirAt(context, ARAccess.VOICES_DIR);
                        }
                        // Increment:
                        tempIndex++;
                    }
                } else ARAccess.createTempDirAt(context, ARAccess.VOICES_DIR);
            }
        }
        // ReRunObserver:
        HomeActivity.setVoicesObserver(true);
        // ARFilesObserver.resetTempVoices();
        // Retuning:
        return voices;
    }

    public static File[] getVoicesFiles() {
        // Initializing(Paths):
        String finalPath = ARAccess.WHATSAPP_VOICES_PATH;
        // Returning:
        return new File(finalPath).listFiles(file -> isVoices(file.getAbsolutePath()));
    }

    public static boolean isVoices(String filePath) {
        // Checking:
        return filePath.endsWith(".opus");
    }

    // Getters:
    public Context getContext() {
        return context;
    }
}