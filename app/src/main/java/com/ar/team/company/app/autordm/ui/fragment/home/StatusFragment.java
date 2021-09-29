package com.ar.team.company.app.autordm.ui.fragment.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ar.team.company.app.autordm.control.adapter.StatusAdapter;
import com.ar.team.company.app.autordm.databinding.FragmentStatusBinding;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class StatusFragment extends Fragment {

    // This for control the Fragment-Layout views:
    private FragmentStatusBinding binding;
    private HomeViewModel model; // MainModel for our fragment.
    // Adapter:
    private StatusAdapter adapter;
    // TAGS:
    @SuppressWarnings("unused")
    private static final String TAG = "StatusFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout:
        binding = FragmentStatusBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Get the fragment layout root.
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing:
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        // StartOperations:
        model.startStatusOperation();
        // Observing:
        model.getStatusLiveData().observe(getViewLifecycleOwner(), this::onStatusChanged);
    }

    // OnStatusChange:
    private void onStatusChanged(List<File> videos) {
        // Loading:
        isLoading(true);
        // Initializing:
        adapter = new StatusAdapter(requireContext(), videos);
        // Preparing(RecyclerView):
        binding.statusRecyclerView.setAdapter(adapter);
        binding.statusRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        // Loading:
        new Handler(Looper.getMainLooper()).postDelayed(() -> isLoading(false), 500);
    }

    @SuppressWarnings("SameParameterValue")
    private void isLoading(boolean loading) {
        // Developing:
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.statusRecyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        // Initializing:
        boolean statusState = model.getStatusThread() != null;
        // Checking(&Interrupting):
        if (statusState) model.getStatusThread().interrupt();
        // Super:
        super.onDestroy();
    }
}