package com.ar.team.company.app.autordm.ar.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ARImagesAccess {

    // Fields:
    private final Context context;
    public static List<File> staticFiles;

    // Constructor:
    public ARImagesAccess(Context context) {
        this.context = context;
    }

    // Method(Static):
    public static List<File> getImagesWithDirs(Context context) {
        // Control:
        HomeActivity.setImagesObserver(false);
        // Initializing:
        ARPreferencesManager manager = new ARPreferencesManager(context, ARPreferencesManager.MODE_FILES);
        File imagesDir = ARAccess.getAppDir(context, ARAccess.IMAGES_DIR);
        File[] whatsAppImagesFiles = getImagesFiles();
        List<File> returningFiles = new ArrayList<>();
        // Debugging:
        Log.d(ARAccess.TAG, "A11-OP: ImagesAccess :: " + imagesDir.getAbsolutePath());
        // Initializing(State):
        boolean state1 = Objects.requireNonNull(imagesDir.listFiles()).length != 0;
        // Looping:
        if (state1) {
            // Checking(Fields):
            String whatsapp = manager.getStringPreferences(ARPreferencesManager.IMAGE_COPIED_FILES);
            StringBuilder copied = new StringBuilder();
            // If it reached to here that's mean that there are already copied images.
            // Now we will start a simple for loop and checking each file by name:
            int tempDebug = 0;
            for (File copiedFile : Objects.requireNonNull(imagesDir.listFiles())) {
                // Checking:
                if (tempDebug == 0) {
                    // Debugging:
                    Log.d(ARAccess.TAG, "A11-OP: ImagesAccess First Copied File :: " + copiedFile.getAbsolutePath());
                    tempDebug++;
                }
                // Checking:
                if (!copiedFile.isDirectory()) {
                    // Getting all files name:
                    copied.append(copiedFile.getName()).append(",");
                    // Adding:
                    returningFiles.add(copiedFile);
                }
            }
            // Checking:
            if (whatsAppImagesFiles != null && whatsAppImagesFiles.length != 0) {
                // We will start checking if file contains this new file or not:
                for (File file : whatsAppImagesFiles) {
                    // Checking:
                    if (!whatsapp.contains(file.getName()) && !copied.toString().contains(file.getName()) && !file.isDirectory()) {
                        // NotifyManager:
                        manager.setStringPreferences(ARPreferencesManager.IMAGE_COPIED_FILES, whatsapp + file.getName() + ",");
                        // Here we will start copy operation because that was new file:
                        ARAccess.copy(file, new File(imagesDir.getAbsolutePath() + "/" + file.getName()));
                        // Debugging:
                        Log.d(ARAccess.TAG, "A11-OP: ImagesAccess Copy Operation Has Been Started For File :: " + file.getName());
                    }
                }
            }
        } else {
            // Initializing:
            int tempIndex = 0;
            // Checking:
            if (whatsAppImagesFiles != null && whatsAppImagesFiles.length != 0) {
                // Looping:
                for (File file : whatsAppImagesFiles) {
                    // NotifyManager:
                    manager.setStringPreferences(ARPreferencesManager.IMAGE_COPIED_FILES, manager.getStringPreferences(ARPreferencesManager.IMAGE_COPIED_FILES) + file.getName() + ",");
                    // Getting first 3 images:
                    if (tempIndex <= 1) {
                        // Start creating temp dir:
                        ARAccess.createTempDirAt(context, ARAccess.IMAGES_DIR);
                        // Debugging:
                        Log.d(ARAccess.TAG, "A11-OP: ImagesAccess Temp Dir Has Been Created");
                    }
                    // Increment:
                    tempIndex++;
                }
            } else ARAccess.createTempDirAt(context, ARAccess.IMAGES_DIR);
        }
        // AddStaticFiles:
        staticFiles = Arrays.asList(Objects.requireNonNull(imagesDir.listFiles()));
        // ReRunObserver:
        HomeActivity.setImagesObserver(true);
        // Debugging:
        Log.d(ARAccess.TAG, "A11-OP: ImagesAccess Is Files Empty :: " + returningFiles.isEmpty());
        // Retuning:
        return returningFiles;
    }

    private static File[] getImagesFiles() {
        // Initializing(Paths):
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String whatsappImagesPath = "/WhatsApp/Media/WhatsApp Images";
        String finalPath = externalStorageDirectory + whatsappImagesPath;
        // Initializing(Paths2):
        String whatsappImagesPath2 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";
        String finalPath2 = externalStorageDirectory + whatsappImagesPath2;
        // FieldsField:
        File[] files = new File(finalPath).listFiles(file -> isImages(file.getAbsolutePath()));
        // Checking:
        try {
            // Initializing:
            String tryingPath = Objects.requireNonNull(files)[0].getAbsolutePath();
            // Returning:
            return files;
        } catch (NullPointerException e) {
            // Returning:
            return new File(finalPath2).listFiles(file -> isImages(file.getAbsolutePath()));
        }
    }

    // InnerClasses:
    @SuppressWarnings("SuspiciousNameCombination")
    public static class ARBitmapHelper {

        // Methods(Decoding):
        public static Bitmap decodeBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(imagePath, options);
        }

        // Methods(Calculating):
        private static int calculateSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
            // Raw Height And Width Of Image:
            final int height = options.outHeight;
            final int width = options.outWidth;
            // SampleSize:
            int inSampleSize = 1;
            // Checking:
            if (height > reqHeight || width > reqWidth) {
                // Initializing:
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Developing:
                // Calculate the largest inSampleSize value that is a power of 2 and
                // keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            // Retuning:
            return inSampleSize;
        }
    }

    // Methods(Checking):
    public static boolean isImages(String filePath) {
        // Checking:
        return filePath.endsWith(".jpg") || filePath.endsWith(".png");
    }

    // Getters:
    public Context getContext() {
        return context;
    }
}