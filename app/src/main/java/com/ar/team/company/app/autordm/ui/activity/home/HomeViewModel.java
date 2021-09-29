package com.ar.team.company.app.autordm.ui.activity.home;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ar.team.company.app.autordm.ar.documents.ARDocumentsAccess;
import com.ar.team.company.app.autordm.ar.images.ARImagesAccess;
import com.ar.team.company.app.autordm.ar.status.ARStatusAccess;
import com.ar.team.company.app.autordm.ar.videos.ARVideosAccess;
import com.ar.team.company.app.autordm.ar.voices.ARVoicesAccess;
import com.ar.team.company.app.autordm.model.Document;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class HomeViewModel extends AndroidViewModel {

    // MainFields(Images):
    private final MutableLiveData<List<Bitmap>> imagesMutableData = new MutableLiveData<>();
    private final LiveData<List<Bitmap>> imagesLiveData = imagesMutableData;
    // MainFields(Videos):
    private final MutableLiveData<List<File>> videosMutableData = new MutableLiveData<>();
    private final LiveData<List<File>> videosLiveData = videosMutableData;
    // MainFields(Voices):
    private final MutableLiveData<List<File>> voicesMutableData = new MutableLiveData<>();
    private final LiveData<List<File>> voicesLiveData = voicesMutableData;
    // MainFields(Status):
    private final MutableLiveData<List<File>> statusMutableData = new MutableLiveData<>();
    private final LiveData<List<File>> statusLiveData = statusMutableData;
    // MainFields(Documents):
    private final MutableLiveData<List<Document>> documentsMutableData = new MutableLiveData<>();
    private final LiveData<List<Document>> documentsLiveData = documentsMutableData;
    // Threads:
    private Thread imagesThread;
    private Thread videosThread;
    private Thread voicesThread;
    private Thread statusThread;
    private Thread documentsThread;

    // Constructor:
    public HomeViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    // Images:
    public void startImageOperation() {
        // Working:
        imagesThread = new Thread(this::imagesThread);
        // StartWorkingThread:
        imagesThread.start();
    }

    // Videos:
    public void startVideoOperation() {
        // Working:
        videosThread = new Thread(this::videosThread);
        // StartWorkingThread:
        videosThread.start();
    }

    // Voices:
    public void startVoiceOperation() {
        // Working:
        voicesThread = new Thread(this::voicesThread);
        // StartWorkingThread:
        voicesThread.start();
    }

    // Status:
    public void startStatusOperation() {
        // Working:
        statusThread = new Thread(this::statusThread);
        // StartWorkingThread:
        statusThread.start();
    }

    // Documents:
    public void startDocumentOperation() {
        // Working:
        documentsThread = new Thread(this::documentsThread);
        // StartWorkingThread:
        documentsThread.start();
    }

    // Method(Thread):
    private void imagesThread() {
        // Initializing:
        List<Bitmap> bitmaps = ARImagesAccess.getImagesWithDirs(getApplication().getApplicationContext());
        // Developing:
        imagesMutableData.postValue(bitmaps);
    }

    private void videosThread() {
        // Initializing:
        List<File> videos = ARVideosAccess.getVideosWithDirs(getApplication().getApplicationContext());
        // Developing:
        videosMutableData.postValue(videos);
    }

    private void voicesThread() {
        // Initializing:
        List<File> voices = ARVoicesAccess.getVoicesWithDirs(getApplication().getApplicationContext());
        // Developing:
        voicesMutableData.postValue(voices);
    }

    private void statusThread() {
        // Initializing:
        List<File> statuses = ARStatusAccess.getStatusWithDirs(getApplication().getApplicationContext());
        // Developing:
        statusMutableData.postValue(statuses);
    }

    private void documentsThread() {
        // Initializing:
        List<Document> documents = ARDocumentsAccess.getDocumentsWithDirs(getApplication().getApplicationContext());
        // Developing:
        documentsMutableData.postValue(documents);
    }

    // Getters(&Setters):
    public MutableLiveData<List<File>> getVideosMutableData() {
        return videosMutableData;
    }

    public LiveData<List<File>> getVideosLiveData() {
        return videosLiveData;
    }

    public MutableLiveData<List<Bitmap>> getImagesMutableData() {
        return imagesMutableData;
    }

    public LiveData<List<Bitmap>> getImagesLiveData() {
        return imagesLiveData;
    }

    public MutableLiveData<List<File>> getVoicesMutableData() {
        return voicesMutableData;
    }

    public LiveData<List<File>> getVoicesLiveData() {
        return voicesLiveData;
    }

    public MutableLiveData<List<Document>> getDocumentsMutableData() {
        return documentsMutableData;
    }

    public LiveData<List<Document>> getDocumentsLiveData() {
        return documentsLiveData;
    }

    public MutableLiveData<List<File>> getStatusMutableData() {
        return statusMutableData;
    }

    public LiveData<List<File>> getStatusLiveData() {
        return statusLiveData;
    }

    public Thread getStatusThread() {
        return statusThread;
    }

    public Thread getDocumentsThread() {
        return documentsThread;
    }

    public Thread getVoicesThread() {
        return voicesThread;
    }

    public Thread getVideosThread() {
        return videosThread;
    }

    public Thread getImagesThread() {
        return imagesThread;
    }
}
