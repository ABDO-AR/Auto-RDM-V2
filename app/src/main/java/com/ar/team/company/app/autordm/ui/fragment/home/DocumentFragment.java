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

import com.ar.team.company.app.autordm.control.adapter.DocumentsAdapter;
import com.ar.team.company.app.autordm.databinding.FragmentDocumentBinding;
import com.ar.team.company.app.autordm.model.Document;
import com.ar.team.company.app.autordm.ui.activity.home.HomeViewModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class DocumentFragment extends Fragment {

    // This for control the Fragment-Layout views:
    private FragmentDocumentBinding binding;
    private HomeViewModel model; // MainModel for our fragment.
    // Adapter:
    private DocumentsAdapter adapter;
    // TAGS:
    @SuppressWarnings("unused")
    private static final String TAG = "DocumentFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout:
        binding = FragmentDocumentBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Get the fragment layout root.
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing:
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        // StartOperations:
        model.startDocumentOperation();
        // Observing:
        model.getDocumentsLiveData().observe(getViewLifecycleOwner(), this::onDocumentsChanged);
    }

    // OnDocumentsChange:
    private void onDocumentsChanged(List<Document> documents) {
        // Loading:
        isLoading(true);
        // Initializing:
        adapter = new DocumentsAdapter(requireContext(), documents);
        // Preparing(RecyclerView):
        binding.recyclerDocumentsView.setAdapter(adapter);
        binding.recyclerDocumentsView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        // Loading:
        new Handler(Looper.getMainLooper()).postDelayed(() -> isLoading(false), 500);
    }

    @SuppressWarnings("SameParameterValue")
    private void isLoading(boolean loading) {
        // Developing:
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.recyclerDocumentsView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        // Initializing:
        boolean documentsState = model.getDocumentsThread() != null;
        // Checking(&Interrupting):
        if (documentsState) model.getDocumentsThread().interrupt();
        // Super:
        super.onDestroy();
    }
}