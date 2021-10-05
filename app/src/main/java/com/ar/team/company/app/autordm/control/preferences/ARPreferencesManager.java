package com.ar.team.company.app.autordm.control.preferences;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class ARPreferencesManager {

    // Fields:
    private SharedPreferences preferences;
    private SharedPreferences filesPreferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor filesEditor;
    // PreferencesName:
    public static final String PREFERENCES_NAME = "ar.Shared.Preferences.Name";
    public static final String FILES_PREFERENCES_NAME = "ar.Shared.Preferences.Files.Name";
    // PreferencesKeys:
    public static final String APP_INIT = "ar.Shared.Preferences.Init.App.Created";
    public static final String PACKAGE_APP_NAME = "ar.Shared.Preferences.Package.App.Name.Value";
    public static final String WHATSAPP_CHATS = "ar.Shared.Preferences.Chats.App.Name.Whatsapp";
    public static final String LIGHT_THEME = "ar.Shared.Preferences.Theme.App.Name.Light.Value";
    public static final String SENDER_NAME = "ar.Shared.Preferences.Chats.Sender.Name.Read";
    public static final String FRAGMENT_NUMBER = "ar.Shared.Preferences.Fragment.Access.Number.Get";
    // AppPreferences:
    public static final String INIT_TEMP_DIR = "ar.Shared.Preferences.Files.Dirs.Create.Temp.Start";
    // WhatsAppPreferences:
    public static final String IMAGE_COPIED_FILES = "ar.Shared.Preferences.Files.Images.Name.Read";
    public static final String VIDEO_COPIED_FILES = "ar.Shared.Preferences.Files.Videos.Name.Read";
    public static final String VOICE_COPIED_FILES = "ar.Shared.Preferences.Files.Voices.Name.Read";
    public static final String STATUS_COPIED_FILES = "ar.Shared.Preferences.Files.Status.Name.Read";
    public static final String DOCUMENTS_COPIED_FILES = "ar.Shared.Preferences.Files.Documents.Name.Read";
    public static final String FRAGMENT_STATE_NUMBER = "ar.Shared.Preferences.Activity.50.Fragment.State.Number";
    // Mode:
    public static final int MODE_CHAT = 0;
    public static final int MODE_FILES = 1;
    // CurrentMode:
    private int currentMode = 0;
    public static String sender="" ;
    //check Chat one

    // Constructors:
    @Deprecated
    public ARPreferencesManager(Context context) {
        // Initializing:
        this.preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        // Developing:
        editor.apply();
    }

    public ARPreferencesManager(Context context, int mode) {
        // Checking:
        if (mode == MODE_CHAT) {
            // Initializing:
            this.preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            this.editor = preferences.edit();
            // Developing:
            editor.apply();
            // CurrentMode:
            currentMode = MODE_CHAT;
        } else if (mode == MODE_FILES) {
            // Initializing:
            this.filesPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            this.filesEditor = filesPreferences.edit();
            // Developing:
            filesEditor.apply();
            // CurrentMode:
            currentMode = MODE_FILES;
        }
    }

    // Setter(Preferences):
    public void setStringPreferences(String key, String value) {
        // Checking:
        if (currentMode == MODE_CHAT){
            // Developing:
            editor.putString(key, value);
            editor.apply();
        }else if (currentMode == MODE_FILES){
            // Developing:
            filesEditor.putString(key, value);
            filesEditor.apply();
        }
    }

    public void setIntegerPreferences(String key, Integer value) {
        // Checking:
        if (currentMode == MODE_CHAT){
            // Developing:
            editor.putInt(key, value);
            editor.apply();
        }else if (currentMode == MODE_FILES){
            // Developing:
            filesEditor.putInt(key, value);
            filesEditor.apply();
        }
    }

    public void setBooleanPreferences(String key, Boolean value) {
        // Checking:
        if (currentMode == MODE_CHAT){
            // Developing:
            editor.putBoolean(key, value);
            editor.apply();
        }else if (currentMode == MODE_FILES){
            // Developing:
            filesEditor.putBoolean(key, value);
            filesEditor.apply();
        }
    }

    // Getter(Preferences):
    public int getCurrentMode() {
        return currentMode;
    }

    public String getStringPreferences(String key) {
        // Initializing:
        String value = "";
        // Checking:
        if (currentMode == MODE_CHAT) value = preferences.getString(key, "Empty,");
        else if (currentMode == MODE_FILES) value = filesPreferences.getString(key, "Empty,");
        // Retuning:
        return value;
    }

    public Integer getIntegerPreferences(String key) {
        // Initializing:
        int value = 0;
        // Checking:
        if (currentMode == MODE_CHAT) value = preferences.getInt(key, 0);
        else if (currentMode == MODE_FILES) value = filesPreferences.getInt(key, 0);
        // Retuning:
        return value;
    }

    public Boolean getBooleanPreferences(String key) {
        // Initializing:
        boolean value = false;
        // Checking:
        if (currentMode == MODE_CHAT) value = preferences.getBoolean(key, false);
        else if (currentMode == MODE_FILES) value = filesPreferences.getBoolean(key, false);
        // Retuning:
        return value;
    }

    public Boolean getThemeBooleanPreferences(String key) {
        // Developing:
        return preferences.getBoolean(key, true);
    }

    // Getters:
    public SharedPreferences getFilesPreferences() {
        return filesPreferences;
    }

    public SharedPreferences.Editor getFilesEditor() {
        return filesEditor;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }
}
