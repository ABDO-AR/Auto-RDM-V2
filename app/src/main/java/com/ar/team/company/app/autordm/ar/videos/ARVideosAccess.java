package com.ar.team.company.app.autordm.ar.videos;

import android.content.Context;
import android.os.Environment;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.control.notifications.ARNotificationManager;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ARVideosAccess {

    // Fields:
    private final Context context;

    // Constructor:
    public ARVideosAccess(Context context) {
        this.context = context;
    }

    // Method(Static):
    public synchronized static List<File> getVideosWithDirs(Context context) {
        // Control:
        HomeActivity.setVideosObserver(false);
        // Initializing:
        ARPreferencesManager manager = new ARPreferencesManager(context, ARPreferencesManager.MODE_FILES);
        File videosDir = ARAccess.getAppDir(context, ARAccess.VIDEOS_DIR);
        File[] whatsAppVideosFiles = getVideosFiles();
        List<File> returningFiles = new ArrayList<>();
        // Initializing(State):
        boolean state1 = Objects.requireNonNull(videosDir.listFiles()).length != 0;
        // Looping:
        if (state1) {
            // Checking(Fields):
            String whatsapp = manager.getStringPreferences(ARPreferencesManager.VIDEO_COPIED_FILES);
            StringBuilder realWhatsApp = new StringBuilder();
            StringBuilder copied = new StringBuilder();
            // If it reached to here that's mean that there are already copied images.
            // Now we will start a simple for loop and checking each file by name:
            for (File copiedFile : Objects.requireNonNull(videosDir.listFiles())) {
                // Checking:
                if (!copiedFile.isDirectory()) {
                    // Getting all files name:
                    copied.append(copiedFile.getName()).append(",");
                }
            }
            // Checking:
            if (whatsAppVideosFiles != null && whatsAppVideosFiles.length != 0) {
                // We will start checking if file contains this new file or not:
                for (File file : whatsAppVideosFiles) {
                    // AddingReal:
                    realWhatsApp.append(file.getName()).append(",");
                    // Checking:
                    if (!whatsapp.contains(file.getName()) && !copied.toString().contains(file.getName()) && !file.isDirectory()) {
                        // NotifyManager:
                        manager.setStringPreferences(ARPreferencesManager.VIDEO_COPIED_FILES, whatsapp + file.getName() + ",");
                        // Here we will start copy operation because that was new file:
                        ARAccess.copy(file, new File(videosDir.getAbsolutePath() + "/" + file.getName()));
                    }
                }
                // Temp:
                int tempDel = 0;
                // LastChecking:
                for (File copiedFile : Objects.requireNonNull(videosDir.listFiles())) {
                    // Checking:
                    if (!copiedFile.isDirectory()) {
                        // Adding:
                        if (!realWhatsApp.toString().contains(copiedFile.getName())) {
                            // Adding(RF):
                            returningFiles.add(copiedFile);
                            // Checking:
                            if (tempDel == 0){
                                // ShowingNotification:
                                ARNotificationManager.showNotification(context, R.string.channel_videos_description, ARNotificationManager.CHANNEL_VIDEOS_ID);
                                // Increment:
                                tempDel++;
                            }
                            // Removing:
                            //startDeletingOperation(copiedFile.getName(), ARPreferencesManager.IMAGE_COPIED_FILES, manager);
                        }
                    }
                }
            }
        } else {
            // Initializing:
            int tempIndex = 0;
            // Checking:
            if (whatsAppVideosFiles != null && whatsAppVideosFiles.length != 0) {
                // Looping:
                for (File file : whatsAppVideosFiles) {
                    // NotifyManager:
                    manager.setStringPreferences(ARPreferencesManager.VIDEO_COPIED_FILES, manager.getStringPreferences(ARPreferencesManager.VIDEO_COPIED_FILES) + file.getName() + ",");
                    // Getting first 3 images:
                    if (tempIndex <= 1) {
                        // Start creating temp dir:
                        ARAccess.createTempDirAt(context, ARAccess.VIDEOS_DIR);
                    }
                    // Increment:
                    tempIndex++;
                }
            } else ARAccess.createTempDirAt(context, ARAccess.VIDEOS_DIR);
        }
        // ReRunObserver:
        HomeActivity.setVideosObserver(true);
        // Retuning:
        return returningFiles;
    }

    public static File[] getVideosFiles() {
        // Initializing(Paths):
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String whatsappImagesPath = "/WhatsApp/Media/WhatsApp Video";
        String finalPath = externalStorageDirectory + whatsappImagesPath;
        // Initializing(Paths2):
        String whatsappImagesPath2 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video";
        String finalPath2 = externalStorageDirectory + whatsappImagesPath2;
        // FieldsField:
        File[] backupFiles = new File(finalPath2).listFiles(file -> isVideos(file.getAbsolutePath()));
        File[] files = new File(finalPath).listFiles(file -> isVideos(file.getAbsolutePath()));
        // Checking:
        if (files == null) files = backupFiles;
        // Returning:
        return files;
    }

    // Methods(Checking):
    public static boolean isVideos(String filePath) {
        // Checking:
        return filePath.endsWith(".mp4");
    }

    // Getters:
    public Context getContext() {
        return context;
    }
}
