package com.ar.team.company.app.autordm.ui.fragment.home;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.control.adapter.MediaAdapter;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.databinding.FragmentMediaBinding;
import com.ar.team.company.app.autordm.model.ARMedia;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;
import com.ar.team.company.app.autordm.ui.interfaces.RemoveListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class MediaFragment extends Fragment {

    // This for control the Fragment-Layout views:
    private FragmentMediaBinding binding;
    private HomeViewModel model; // MainModel for our fragment.
    // Manager:
    private ARPreferencesManager manager;
    // Adapter:
    private MediaAdapter adapter;
    // TAGS:
    @SuppressWarnings("unused")
    public static final String TAG = "MediaFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout:
        binding = FragmentMediaBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Get the fragment layout root.
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing:
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        manager = new ARPreferencesManager(requireContext());
        // StartOperations:
        model.startMediaOperations();
        // Observing:
        model.getMediaLiveData().observe(getViewLifecycleOwner(), this::onMediaChanged);
        // Developing:
        binding.fabDeleteAll.setOnClickListener(this::clearAppData);
    }

    // Clearing all data:
    @SuppressLint("NotifyDataSetChanged")
    private void clearAppData(View view) {
        // Initializing:
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        // Setting:
        builder.setTitle(getString(R.string.data_dialog_title));
        builder.setMessage(getString(R.string.data_dialog_des));
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        // Developing:
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> startDeleteMediaContent());
        builder.setNegativeButton(android.R.string.no, null);
        // Showing:
        builder.show();
    }

    private void startDeleteMediaContent(){
        // Checking:
        if (adapter != null) {
            // Initializing:
            List<File> files = adapter.getMedia().getContent();
            // Lopping:
            for (File file : files) {
                // Checking:
                if (!manager.getStringPreferences(ARPreferencesManager.REMOVING_FILES_PREF).contains(file.getName())){
                    // Removing:
                    manager.setStringPreferences(ARPreferencesManager.REMOVING_FILES_PREF, manager.getStringPreferences(ARPreferencesManager.REMOVING_FILES_PREF) + file.getName() + ",");
                }
            }
            // Initializing(Temp):
            List<File> tempFiles = new ArrayList<>();
            String removedFiles = manager.getStringPreferences(ARPreferencesManager.REMOVING_FILES_PREF);
            // Looping:
            for (File file : files) {
                // Checking:
                if (!removedFiles.contains(file.getName())) tempFiles.add(file);
            }
            // Resetting:
            adapter.getMedia().getContent().clear();
            adapter.getMedia().getContent().addAll(tempFiles);
            // Notify
            adapter.notifyDataSetChanged();
        }
    }

    // OnMediaChange:
    private void onMediaChanged(ARMedia media) {
        // Loading:
        isLoading(true);
        // Initializing:
        adapter = new MediaAdapter(requireContext(), media);
        // Preparing(RecyclerView):
        binding.mediaRecyclerView.setAdapter(adapter);
        binding.mediaRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Loading:
        new Handler(Looper.getMainLooper()).postDelayed(() -> isLoading(false), 500);
    }

    @SuppressWarnings("SameParameterValue")
    private void isLoading(boolean loading) {
        // Developing:
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.mediaRecyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        // Initializing:
        boolean mediaState = model.getMediaThread() != null;
        // Checking(&Interrupting):
        if (mediaState) model.getMediaThread().interrupt();
        // Super:
        super.onDestroy();
    }
}