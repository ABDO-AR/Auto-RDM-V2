package com.ar.team.company.app.autordm.ui.fragment.home;

import android.graphics.Bitmap;
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

import com.ar.team.company.app.autordm.control.adapter.ImagesAdapter;
import com.ar.team.company.app.autordm.databinding.FragmentImagesBinding;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ImagesFragment extends Fragment {

    // This for control the Fragment-Layout views:
    private FragmentImagesBinding binding;
    private HomeViewModel model; // MainModel for our fragment.
    // Adapter:
    private ImagesAdapter adapter;
    // TAGS:
    private static final String TAG = "ImagesFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout:
        binding = FragmentImagesBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Get the fragment layout root.
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing:
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        // StartOperations:
        model.startImageOperation();
        // Observing:
        model.getImagesLiveData().observe(getViewLifecycleOwner(), this::onBitmapsChanged);
    }

    // OnBitmapsChange:
    private void onBitmapsChanged(List<Bitmap> bitmaps) {
        // Loading:
        isLoading(true);
        // Initializing:
        adapter = new ImagesAdapter(requireContext(), bitmaps);
        // Preparing(RecyclerView):
        binding.recyclerImageView.setAdapter(adapter);
        binding.recyclerImageView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        // Loading:
        new Handler(Looper.getMainLooper()).postDelayed(() -> isLoading(false), 500);
    }

    @SuppressWarnings("SameParameterValue")
    private void isLoading(boolean loading) {
        // Developing:
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.recyclerImageView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        // Initializing:
        boolean imagesState = model.getImagesThread() != null;
        // Checking(&Interrupting):
        if (imagesState) model.getImagesThread().interrupt();
        // Super:
        super.onDestroy();
    }
}