package com.ar.team.company.app.autordm.ui.fragment.home;

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

import com.ar.team.company.app.autordm.control.adapter.MediaAdapter;
import com.ar.team.company.app.autordm.databinding.FragmentMediaBinding;
import com.ar.team.company.app.autordm.model.ARMedia;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldCanBeLocal")
public class MediaFragment extends Fragment {

    // This for control the Fragment-Layout views:
    private FragmentMediaBinding binding;
    private HomeViewModel model; // MainModel for our fragment.
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
        // StartOperations:
        model.startMediaOperations();
        // Observing:
        model.getMediaLiveData().observe(getViewLifecycleOwner(), this::onMediaChanged);
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