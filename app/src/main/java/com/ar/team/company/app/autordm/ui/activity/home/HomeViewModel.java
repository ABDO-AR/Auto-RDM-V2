package com.ar.team.company.app.autordm.ui.activity.home;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ar.team.company.app.autordm.ar.documents.ARDocumentsAccess;
import com.ar.team.company.app.autordm.ar.images.ARImagesAccess;
import com.ar.team.company.app.autordm.ar.videos.ARVideosAccess;
import com.ar.team.company.app.autordm.ar.voices.ARVoicesAccess;
import com.ar.team.company.app.autordm.control.adapter.MediaAdapter;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.model.ARMedia;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class HomeViewModel extends AndroidViewModel {

    // MainFields(Images):
    private final MutableLiveData<ARMedia> mediaMutableData = new MutableLiveData<>();
    private final LiveData<ARMedia> mediaLiveData = mediaMutableData;
    // Threads:
    private Thread mediaThread;

    // Constructor:
    public HomeViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    // Media:
    public void startMediaOperations() {
        // Working:
        mediaThread = new Thread(this::mediaThread);
        // StartWorkingThread:
        if (isPackageInstalled("com.whatsapp", getApplication().getPackageManager())) mediaThread.start();
        else Toast.makeText(getApplication().getApplicationContext(), "Whatsapp not found in this device", Toast.LENGTH_SHORT).show();
    }

    // Method(Thread):
    private void mediaThread() {
        // Initializing:
        ARPreferencesManager manager = new ARPreferencesManager(getApplication().getApplicationContext());
        List<File> images = ARImagesAccess.getImagesWithDirs(getApplication().getApplicationContext());
        List<File> videos = ARVideosAccess.getVideosWithDirs(getApplication().getApplicationContext());
        List<File> voices = ARVoicesAccess.getVoicesWithDirs(getApplication().getApplicationContext());
        List<File> documents = ARDocumentsAccess.getDocumentsWithDirs(getApplication().getApplicationContext());
        // Initializing(Content-Filed):
        List<File> content = new ArrayList<>();
        // Adding(Content):
        content.addAll(images);
        content.addAll(videos);
        content.addAll(voices);
        content.addAll(documents);
        // Initializing(Sorting):
        File[] sortingContent = content.toArray(new File[0]);
        // Sorting:
        Arrays.sort(sortingContent, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        // Resetting(Content):
        content.clear();
        content.addAll(Arrays.asList(sortingContent));
        // Initializing(Returning-Filed):
        ARMedia media = new ARMedia(content);
        // Initializing(Temp):
        List<File> temp = new ArrayList<>();
        String removedFiles = manager.getStringPreferences(ARPreferencesManager.REMOVING_FILES_PREF);
        // Looping:
        for (File file : media.getContent()) {
            // Checking(&Removing):
            if (!removedFiles.contains(file.getName())) temp.add(file);
        }
        // Clearing(&ReAdding):
        media.getContent().clear();
        media.getContent().addAll(temp);
        // Debugging:
        Log.d(MediaAdapter.TAG, "MediaAdapter: " + removedFiles);
        // Developing:
        mediaMutableData.postValue(media);
    }

    // Method(Check):
    @SuppressWarnings("SameParameterValue")
    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        // Trying:
        try {
            // Checking:
            packageManager.getPackageInfo(packageName, 0);
            // Returning:
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // Retuning:
            return false;
        }
    }

    // Getters(&Setters):
    public MutableLiveData<ARMedia> getMediaMutableData() {
        return mediaMutableData;
    }

    public LiveData<ARMedia> getMediaLiveData() {
        return mediaLiveData;
    }

    public Thread getMediaThread() {
        return mediaThread;
    }
}
