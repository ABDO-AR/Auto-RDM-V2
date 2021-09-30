package com.ar.team.company.app.autordm.ar.voices;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.ar.observer.ARFilesObserver;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ARVoicesAccess {

    // Fields:
    private final Context context;

    // Constructor:
    public ARVoicesAccess(Context context) {
        this.context = context;
    }

    // Method(Static):
    public static List<File> getVoicesWithDirs(Context context) {
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
            StringBuilder copied = new StringBuilder();
            // If it reached to here that's mean that there are already copied images.
            // Now we will start a simple for loop and checking each file by name:
            for (File copiedFile : Objects.requireNonNull(voicesDir.listFiles())) {
                // Checking:
                if (!copiedFile.isDirectory()) {
                    // Getting all files name:
                    copied.append(copiedFile.getName()).append(",");
                    // Adding:
                    voices.add(copiedFile);
                }
            }
            // Checking:
            if (whatsAppVoicesFiles != null && whatsAppVoicesFiles.length != 0) {
                // We will start checking if file contains this new file or not:
                for (File file : whatsAppVoicesFiles) {
                    // Checking:
                    if (!whatsapp.contains(file.getName()) && !copied.toString().contains(file.getName()) && !file.isDirectory()) {
                        // NotifyManager:
                        manager.setStringPreferences(ARPreferencesManager.VOICE_COPIED_FILES, whatsapp + file.getName() + ",");
                        // Here we will start copy operation because that was new file:
                        ARAccess.copy(file, new File(voicesDir.getAbsolutePath() + "/" + file.getName()));
                    }
                }
            }
            // Debugging:
            Log.d(ARAccess.TAG, "A11-OP: VoicesAccess WhatsApp Files Name :: " + whatsapp);
        } else {
            // Initializing:
            int tempIndex = 0;
            // Checking:
            if (whatsAppVoicesFiles != null && whatsAppVoicesFiles.length != 0) {
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
        // ReRunObserver:
        ARFilesObserver.resetTempVoices();
        HomeActivity.setVoicesObserver(true);
        // Retuning:
        return voices;
    }

    public static File[] getVoicesFiles() {
        // Initializing(Paths):
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String whatsappImagesPath = "/WhatsApp/Media/WhatsApp Voice Notes"; // (/202138).
        String finalPath = externalStorageDirectory + whatsappImagesPath;
        // Getting:
        File[] dirs = new File(finalPath).listFiles();
        if (dirs == null) {
            // Initializing(Paths2):
            String whatsappImagesPath2 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes";
            String finalPath2 = externalStorageDirectory + whatsappImagesPath2;
            // FieldsField:
            dirs = new File(finalPath2).listFiles(file -> isVoices(file.getAbsolutePath()));
        }
        List<File> files = new ArrayList<>();
        // Checking(&Developing):
        for (File dir : Objects.requireNonNull(dirs)) {
            if (dir.isDirectory()) {
                // Initializing:
                File[] voices = dir.listFiles(file -> isVoices(file.getAbsolutePath()));
                // Developing:
                files.addAll(Arrays.asList(Objects.requireNonNull(voices)));
            } else if (dir.getAbsolutePath().endsWith(".opus")) files.add(dir);
        }
        // Returning:
        return files.toArray(new File[0]);
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