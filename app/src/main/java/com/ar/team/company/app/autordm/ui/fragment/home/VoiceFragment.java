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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ar.team.company.app.autordm.control.adapter.VoicesAdapter;
import com.ar.team.company.app.autordm.databinding.FragmentVoiceBinding;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class VoiceFragment extends Fragment {

    // This for control the Fragment-Layout views:
    private FragmentVoiceBinding binding;
    private HomeViewModel model; // MainModel for our fragment.
    // Adapter:
    private VoicesAdapter adapter;
    // TAGS:
    private static final String TAG = "VoiceFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout:
        binding = FragmentVoiceBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Get the fragment layout root.
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing:
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        // StartOperations:
        model.startVoiceOperation();
        // Observing:
        model.getVoicesLiveData().observe(getViewLifecycleOwner(), this::onVoicesChanged);
    }

    // OnVoicesChange:
    private void onVoicesChanged(List<File> voices) {
        // Loading:
        isLoading(true);
        // Initializing:
        adapter = new VoicesAdapter(requireContext(), voices);
        // Preparing(RecyclerView):
        binding.voicesRecyclerView.setAdapter(adapter);
        binding.voicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Loading:
        new Handler(Looper.getMainLooper()).postDelayed(() -> isLoading(false), 500);
    }

    @SuppressWarnings("SameParameterValue")
    private void isLoading(boolean loading) {
        // Developing:
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.voicesRecyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        // Initializing:
        boolean voicesState = model.getVoicesThread() != null;
        // Checking(&Interrupting):
        if (voicesState) model.getVoicesThread().interrupt();
        // Super:
        super.onDestroy();
    }
}