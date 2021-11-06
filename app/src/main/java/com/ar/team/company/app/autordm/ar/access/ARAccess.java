package com.ar.team.company.app.autordm.ar.access;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ARAccess {

    // MainFields:
    public static Map<String, File> MAIN_FILE_MAP;
    // Fields(Root):
    public static final String ROOT_DIR = "AutoRDM App";
    // Fields(SubRoot):
    public static final String IMAGES_DIR = ROOT_DIR + " Images";
    public static final String VIDEOS_DIR = ROOT_DIR + " Videos";
    public static final String VOICES_DIR = ROOT_DIR + " Voices";
    public static final String STATUS_DIR = ROOT_DIR + " Status";
    public static final String DOCUMENTS_DIR = ROOT_DIR + " Document";
    // Fields(Paths):
    public static final String WHATSAPP_IMAGES_PATH = getWhatsappPaths(IMAGES_DIR);
    public static final String WHATSAPP_VIDEOS_PATH = getWhatsappPaths(VIDEOS_DIR);
    public static final String WHATSAPP_VOICES_PATH = getWhatsappPaths(VOICES_DIR);
    public static final String WHATSAPP_STATUS_PATH = getWhatsappPaths(STATUS_DIR);
    public static final String WHATSAPP_DOCUMENTS_PATH = getWhatsappPaths(DOCUMENTS_DIR);
    // Fields(Temp):
    public static final String TEMP_DIR = "SD--TEMP--DIR";
    // Fields(Debug):
    public static final String TAG = "ARAccess";

    // Methods(Main):
    public static File getAppDir(Context context, String dir) {
        // Checking:
        if (MAIN_FILE_MAP == null) {
            // Initializing:
            createAccessDir(context, ROOT_DIR);
            // Developing:
            MAIN_FILE_MAP = createAccessDirs(context, IMAGES_DIR, VIDEOS_DIR, VOICES_DIR, STATUS_DIR, DOCUMENTS_DIR);
        }
        // Debugging:
        Log.d(TAG, "A11-OP: Start Getting Dir :: " + dir);
        // Returning:
        return MAIN_FILE_MAP.get(dir);
    }

    // Methods(Path):
    public static String getWhatsappPaths(String dir) {
        // Initializing:
        String env = Environment.getExternalStorageDirectory().getAbsolutePath();
        String returningPath = "";
        // Preparing
        switch (dir) {
            case IMAGES_DIR:
                returningPath = getPaths("/WhatsApp/Media/WhatsApp Images", "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images");
                break;
            case VIDEOS_DIR:
                returningPath = getPaths("/WhatsApp/Media/WhatsApp Video", "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video");
                break;
            case VOICES_DIR:
                returningPath = getPaths("/WhatsApp/Media/WhatsApp Voice Notes", "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes");
                break;
            case STATUS_DIR:
                returningPath = getPaths("/WhatsApp/Media/.Statuses", "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses");
                break;
            case DOCUMENTS_DIR:
                returningPath = getPaths("/WhatsApp/Media/WhatsApp Documents", "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Documents");
                break;
        }
        // Retuning:
        return returningPath;
    }

    public static String getPaths(String dir, String dir2) {
        // Initializing(Paths):
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String finalPath = externalStorageDirectory + dir;
        // Initializing(Paths2):
        String finalPath2 = externalStorageDirectory + dir2;
        // FieldsField:
        File file = new File(finalPath);
        int[] longsData = new int[0];
        // Checking:
        if (file.exists()) {
            // Checking:
            if (dir.contains("WhatsApp Voice Notes") || dir2.contains("WhatsApp Voice Notes")) {
                // Checking:
                if (file.listFiles() != null) {
                    // Initializing:
                    File[] files = file.listFiles();
                    StringBuilder longs = new StringBuilder();
                    Pattern pattern = Pattern.compile("[0-9]+");
                    // Looping:
                    for (int index = 0; index < Objects.requireNonNull(files).length; index++) {
                        // Init:
                        String dirName = files[index].getName();
                        // Checking:
                        if (pattern.matcher(dirName).matches()) longs.append(dirName).append(",");
                    }
                    // Preparing:
                    String[] parsedLongs = longs.toString().split(",");
                    longsData = new int[parsedLongs.length];
                    // Looping:
                    for (int index = 0; index < parsedLongs.length; index++) {
                        // Adding:
                        longsData[index] = Integer.parseInt(parsedLongs[index]);
                    }
                }
                // Returning:
                return finalPath + "/" + getLargest(longsData, longsData.length);
            }
            // Returning:
            return finalPath;
        } else {
            // Checking:
            if (dir.contains("WhatsApp Voice Notes") || dir2.contains("WhatsApp Voice Notes")) {
                // Checking:
                if (file.listFiles() != null) {
                    // Initializing:
                    File[] files = file.listFiles();
                    StringBuilder longs = new StringBuilder();
                    Pattern pattern = Pattern.compile("[0-9]+");
                    // Looping:
                    for (int index = 0; index < Objects.requireNonNull(files).length; index++) {
                        // Init:
                        String dirName = files[index].getName();
                        // Checking:
                        if (pattern.matcher(dirName).matches()) longs.append(dirName).append(",");
                    }
                    // Preparing:
                    String[] parsedLongs = longs.toString().split(",");
                    longsData = new int[parsedLongs.length];
                    // Looping:
                    for (int index = 0; index < parsedLongs.length; index++) {
                        // Adding:
                        longsData[index] = Integer.parseInt(parsedLongs[index]);
                    }
                }
                // Returning:
                return finalPath + "/" + getLargest(longsData, longsData.length);
            }
            // Returning:
            return finalPath2;
        }
    }

    // Method(LargestArrayNumber):
    private static int getLargest(int[] a, int total) {
        // Initializing:
        int temp;
        // Looping:
        for (int i = 0; i < total; i++) {
            // Looping:
            for (int j = i + 1; j < total; j++) {
                if (a[i] > a[j]) {
                    // Preparing:
                    temp = a[i];
                    // Checking(Preparing):
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        // Returning:
        return a[total - 1];
    }

    // Methods(Access):
    @SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue"})
    public static File createAccessDir(Context context, String dir) {
        // Initializing:
        File accessDir = new File(context.getExternalFilesDir(null), dir);
        // Debugging:
        Log.d(TAG, "createAccessDir: " + accessDir.getAbsolutePath());
        // Checking:
        if (!accessDir.exists()) {
            // Checking:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Trying:
                try {
                    // Creating:
                    Files.createDirectory(Paths.get(accessDir.getAbsolutePath()));
                    // Debugging:
                    Log.d(TAG, "A11-OP: Media Dir Has Been Created At :: " + accessDir.getAbsolutePath());
                } catch (IOException e) {
                    // Debugging:
                    Log.d(TAG, "createAccessDir: " + e.toString());
                }
            } else accessDir.mkdir();
        }
        // Retuning:
        return accessDir;
    }

    // Methods(AccessDirs):
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Map<String, File> createAccessDirs(Context context, String... dirs) {
        // Initializing:
        Map<String, File> fileMap = new HashMap<>();
        // Looping:
        for (String dir : dirs) {
            // Initializing:
            File accessDir = new File(context.getExternalFilesDir(null), ROOT_DIR + "/" + dir);
            // Checking:
            if (!accessDir.exists()) {
                // Checking:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Trying:
                    try {
                        // Creating:
                        Files.createDirectory(Paths.get(accessDir.getAbsolutePath()));
                        // Debugging:
                        Log.d(TAG, "A11-OP: Media dir has been created at :: " + accessDir.getAbsolutePath());
                    } catch (IOException e) {
                        // Debugging:
                        Log.d(TAG, "createAccessDir: " + e.toString());
                    }
                } else accessDir.mkdir();
                // Adding:
                fileMap.put(dir, accessDir);
            } else {
                // It's already exits:
                fileMap.put(dir, new File(context.getExternalFilesDir(null), ROOT_DIR + "/" + dir));
            }
        }
        // Retuning:
        return fileMap;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createTempDirAt(Context context, String dir) {
        // Debugging:
        Log.d(TAG, "A11-OP: Start Creating TEMP-DIR At :: " + dir);
        // Initializing:
        File tempDir = new File(context.getExternalFilesDir(null), ROOT_DIR + "/" + dir + "/" + TEMP_DIR);
        // Checking:
        if (!tempDir.exists()) {
            // Checking:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Trying:
                try {
                    // Creating:
                    Files.createDirectory(Paths.get(tempDir.getAbsolutePath()));
                    // Debugging:
                    Log.d(TAG, "A11-OP: TEMP-DIR Has Been Created At :: " + tempDir.getAbsolutePath());
                } catch (IOException e) {
                    // Debugging:
                    Log.d(TAG, "createAccessDir: " + e.toString());
                    // Debugging:
                    Log.d(TAG, "A11-OP: TEMP-DIR Error :: C1");
                }
            } else tempDir.mkdir();
        } else {
            // It's already exits:
            Log.d(TAG, "createTempDirAt: temp dir is already exits at :: " + tempDir.getAbsolutePath());
            // Debugging:
            Log.d(TAG, "A11-OP: TEMP-DIR Already Exits At :: " + tempDir.getAbsolutePath());
        }
    }

    public static void copy(File src, File dst) {
        // Debugging:
        Log.d(TAG, "A11-OP: Files Start Copy Operations");
        // Trying:
        try (InputStream in = new FileInputStream(src)) {
            // Trying:
            try (OutputStream out = new FileOutputStream(dst)) {
                // Initializing:
                byte[] buf = new byte[1024];
                int len;
                // Looping:
                while ((len = in.read(buf)) > 0) {
                    // Writing:
                    out.write(buf, 0, len);
                }
            } catch (IOException e) {
                // StackTrace:
                e.printStackTrace();
                // Debugging:
                Log.d(TAG, "A11-OP: Files Error While Coping :: C1");
            }
        } catch (IOException e) {
            // StackTrace:
            e.printStackTrace();
            // Debugging:
            Log.d(TAG, "A11-OP: Files Error While Coping :: C2");
        }
        // Debugging:
        Log.d(TAG, "A11-OP: Files Copied Successfully");
    }
}
