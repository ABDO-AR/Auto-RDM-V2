package com.ar.team.company.app.autordm.ar.status;

import android.content.Context;
import android.os.Environment;

import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ARStatusAccess {

    // Fields:
    private final Context context;

    // Constructor:
    public ARStatusAccess(Context context) {
        this.context = context;
    }

    // Method(Static):
    public static List<File> getStatusWithDirs(Context context) {
        // Control:
        HomeActivity.setStatusObserver(false);
        // Initializing:
        ARPreferencesManager manager = new ARPreferencesManager(context, ARPreferencesManager.MODE_FILES);
        File statusDir = ARAccess.getAppDir(context, ARAccess.STATUS_DIR);
        File[] whatsAppStatusFiles = getStatusFiles();
        List<File> returningFiles = new ArrayList<>();
        // Initializing(State):
        boolean state1 = Objects.requireNonNull(statusDir.listFiles()).length != 0;
        // Looping:
        if (state1) {
            // Checking(Fields):
            String whatsapp = manager.getStringPreferences(ARPreferencesManager.STATUS_COPIED_FILES);
            StringBuilder copied = new StringBuilder();
            // If it reached to here that's mean that there are already copied images.
            // Now we will start a simple for loop and checking each file by name:
            for (File copiedFile : Objects.requireNonNull(statusDir.listFiles())) {
                // Checking:
                if (!copiedFile.isDirectory()) {
                    // Getting all files name:
                    copied.append(copiedFile.getName()).append(",");
                    // Adding:
                    returningFiles.add(copiedFile);
                }
            }
            // Checking:
            if (whatsAppStatusFiles != null && whatsAppStatusFiles.length != 0) {
                // We will start checking if file contains this new file or not:
                for (File file : whatsAppStatusFiles) {
                    // Checking:
                    if (!whatsapp.contains(file.getName()) && !copied.toString().contains(file.getName()) && !file.isDirectory()) {
                        // NotifyManager:
                        manager.setStringPreferences(ARPreferencesManager.STATUS_COPIED_FILES, whatsapp + file.getName() + ",");
                        // Here we will start copy operation because that was new file:
                        ARAccess.copy(file, new File(statusDir.getAbsolutePath() + "/" + file.getName()));
                    }
                }
            }
        } else {
            // Initializing:
            int tempIndex = 0;
            // Checking:
            if (whatsAppStatusFiles != null && whatsAppStatusFiles.length != 0) {
                // Looping:
                for (File file : whatsAppStatusFiles) {
                    // NotifyManager:
                    manager.setStringPreferences(ARPreferencesManager.STATUS_COPIED_FILES, manager.getStringPreferences(ARPreferencesManager.STATUS_COPIED_FILES) + file.getName() + ",");
                    // Getting first 3 images:
                    if (tempIndex <= 1) {
                        // Start creating temp dir:
                        ARAccess.createTempDirAt(context, ARAccess.STATUS_DIR);
                    }
                    // Copying:
                    ARAccess.copy(file, new File(statusDir.getAbsolutePath() + "/" + file.getName()));
                    returningFiles.add(file);
                    // Increment:
                    tempIndex++;
                }
            } else ARAccess.createTempDirAt(context, ARAccess.STATUS_DIR);
        }
        // ReRunObserver:
        HomeActivity.setStatusObserver(true);
        // Retuning:
        return returningFiles;
    }

    public static File[] getStatusFiles() {
        // Initializing(Paths):
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String whatsappImagesPath = "/WhatsApp/Media/.Statuses";
        String finalPath = externalStorageDirectory + whatsappImagesPath;
        // Initializing(Paths2):
        String whatsappImagesPath2 = "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        String finalPath2 = externalStorageDirectory + whatsappImagesPath2;
        // FieldsField:
        File[] backupFiles = new File(finalPath2).listFiles(file -> isStatus(file.getAbsolutePath()));
        File[] files = new File(finalPath).listFiles(file -> isStatus(file.getAbsolutePath()));
        // Checking:
        if (files == null || files.length <= 0) files = backupFiles;
        // Returning:
        return files;
    }

    // Methods(Checking):
    public static boolean isStatus(String filePath) {
        // Checking:
        return filePath.endsWith(".mp4") || filePath.endsWith(".jpg") || filePath.endsWith(".png");
    }

    // Getters:
    public Context getContext() {
        return context;
    }
}
