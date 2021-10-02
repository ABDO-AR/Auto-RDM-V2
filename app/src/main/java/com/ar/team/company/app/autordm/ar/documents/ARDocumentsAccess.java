package com.ar.team.company.app.autordm.ar.documents;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.model.Document;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ARDocumentsAccess {

    // Fields:
    private final Context context;

    // Constructor:
    public ARDocumentsAccess(Context context) {
        this.context = context;
    }

    // Method(Static):
    public synchronized static List<File> getDocumentsWithDirs(Context context) {
        // Control:
        HomeActivity.setDocumentsObserver(false);
        // Initializing:
        ARPreferencesManager manager = new ARPreferencesManager(context, ARPreferencesManager.MODE_FILES);
        File documentsDir = ARAccess.getAppDir(context, ARAccess.DOCUMENTS_DIR);
        File[] whatsAppDocumentsFiles = getDocumentsFiles();
        List<File> returningFiles = new ArrayList<>();
        // Initializing(State):
        boolean state1 = Objects.requireNonNull(documentsDir.listFiles()).length != 0;
        // Looping:
        if (state1) {
            // Checking(Fields):
            String whatsapp = manager.getStringPreferences(ARPreferencesManager.DOCUMENTS_COPIED_FILES);
            StringBuilder copied = new StringBuilder();
            // If it reached to here that's mean that there are already copied images.
            // Now we will start a simple for loop and checking each file by name:
            for (File copiedFile : Objects.requireNonNull(documentsDir.listFiles())) {
                // Checking:
                if (!copiedFile.isDirectory()) {
                    // Getting all files name:
                    copied.append(copiedFile.getName()).append(",");
                    // Adding:
                    returningFiles.add(copiedFile);
                }
            }
            // Checking:
            if (whatsAppDocumentsFiles != null && whatsAppDocumentsFiles.length != 0) {
                // We will start checking if file contains this new file or not:
                for (File file : whatsAppDocumentsFiles) {
                    // Checking:
                    if (!whatsapp.contains(file.getName()) && !copied.toString().contains(file.getName()) && !file.isDirectory()) {
                        // NotifyManager:
                        manager.setStringPreferences(ARPreferencesManager.DOCUMENTS_COPIED_FILES, whatsapp + file.getName() + ",");
                        // Here we will start copy operation because that was new file:
                        ARAccess.copy(file, new File(documentsDir.getAbsolutePath() + "/" + file.getName()));
                    }
                }
            }
        } else {
            // Initializing:
            int tempIndex = 0;
            // Checking:
            if (whatsAppDocumentsFiles != null && whatsAppDocumentsFiles.length != 0) {
                // Looping:
                for (File file : whatsAppDocumentsFiles) {
                    // NotifyManager:
                    manager.setStringPreferences(ARPreferencesManager.DOCUMENTS_COPIED_FILES, manager.getStringPreferences(ARPreferencesManager.DOCUMENTS_COPIED_FILES) + file.getName() + ",");
                    // Getting first 3 images:
                    if (tempIndex <= 1) {
                        // Start creating temp dir:
                        ARAccess.createTempDirAt(context, ARAccess.DOCUMENTS_DIR);
                    }
                    // Increment:
                    tempIndex++;
                }
            } else ARAccess.createTempDirAt(context, ARAccess.DOCUMENTS_DIR);
        }
        // ReRunObserver:
        HomeActivity.setDocumentsObserver(true);
        // Retuning:
        return returningFiles;
    }

    public static String getFileSize(File file) {
        // Initializing:
        String fileSize;
        long kb = (file.length() / 1024);
        // Checking:
        if (kb > 1000) fileSize = (kb / 1024) + " MB";
        else fileSize = kb + " KB";
        // Returning:
        return fileSize;
    }

    public static File[] getDocumentsFiles() {
        // Initializing(Paths):
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String whatsappImagesPath = "/WhatsApp/Media/WhatsApp Documents";
        String finalPath = externalStorageDirectory + whatsappImagesPath;
        // Initializing(Paths2):
        String whatsappImagesPath2 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Documents";
        String finalPath2 = externalStorageDirectory + whatsappImagesPath2;
        // FieldsField:
        File[] backupFiles = new File(finalPath2).listFiles();
        File[] files = new File(finalPath).listFiles();
        // Checking:
        if (files == null) files = backupFiles;
        // Returning:
        return files;
    }

    // Getters:
    public Context getContext() {
        return context;
    }
}
