package com.ar.team.company.app.autordm.ui.activity.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ar.team.company.app.autordm.ar.documents.ARDocumentsAccess;
import com.ar.team.company.app.autordm.ar.images.ARImagesAccess;
import com.ar.team.company.app.autordm.ar.videos.ARVideosAccess;
import com.ar.team.company.app.autordm.ar.voices.ARVoicesAccess;
import com.ar.team.company.app.autordm.model.ARMedia;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
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
    public void startMediaOperations(){
        // Working:
        mediaThread = new Thread(this::mediaThread);
        // StartWorkingThread:
        mediaThread.start();
    }

    // Method(Thread):
    private void mediaThread() {
        // Initializing:
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
        // Initializing(Returning-Filed):
        ARMedia media = new ARMedia(content);
        // Developing:
        mediaMutableData.postValue(media);
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
