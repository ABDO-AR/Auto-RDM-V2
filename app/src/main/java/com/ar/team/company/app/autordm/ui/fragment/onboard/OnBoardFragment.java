package com.ar.team.company.app.autordm.ui.fragment.onboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.permissions.ARPermissionsRequest;
import com.ar.team.company.app.autordm.control.adapter.BoardAdapter;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.databinding.FragmentOnBoardBinding;
import com.ar.team.company.app.autordm.model.Board;
import com.ar.team.company.app.autordm.ar.utils.ARUtils;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class OnBoardFragment extends Fragment {

    // This for control the Fragment-Layout views:
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private FragmentOnBoardBinding binding;
    // ViewPager:
    private BoardAdapter adapter;
    private List<Board> boards;
    private TextView[] textViews;
    private int index = 0;
    // ARPermissionsRequest:
    private ARPermissionsRequest request;
    // Preferences:
    private ARPreferencesManager manager;
    // TAGS:
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static final String TAG = "OnBoardFragment";
    private static final String perm = Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout:
        binding = FragmentOnBoardBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Get the fragment layout root.
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // InitializingPreferences:
        manager = new ARPreferencesManager(requireContext());
        request = new ARPermissionsRequest(requireContext());
        // Initializing:
        initUI();
        initPager();
        // Developing:
        binding.nextBoardLayout.setOnClickListener(this::moveToNextPage);
        binding.onBoardPager.setAdapter(adapter);
        binding.onBoardPager.registerOnPageChangeCallback(new ARPagerCallBack());
    }

    // Initializing User Interface:
    private void initUI() {
        // Initializing:
        View decorView = requireActivity().getWindow().getDecorView();
        // Developing:
        decorView.setSystemUiVisibility(View.FOCUSABLES_ALL);
    }

    // OurPagerCallBack(Class):
    class ARPagerCallBack extends ViewPager2.OnPageChangeCallback {

        // Fields:
        private final ActivityResultContracts.RequestPermission request = new ActivityResultContracts.RequestPermission();
        private final ActivityResultLauncher<String> launcher = registerForActivityResult(request, this::permResult);

        @Override
        public void onPageSelected(int position) {
            // Setting index for control:
            index = position;
            // CheckingDots:
            for (int i = 0; i < textViews.length; i++) {
                if (i == position) {
                    textViews[position].setTextSize(30);
                    textViews[position].setTextColor(Color.WHITE);
                } else {
                    textViews[i].setTextSize(25);
                    textViews[i].setTextColor(Color.GRAY);
                }
            }
            // StartAskStoragePermission:
            if (position == 1) {
                // Initializing:
                enabled(false);
                // Developing:
                if (ContextCompat.checkSelfPermission(requireContext(), perm) == PackageManager.PERMISSION_GRANTED) {
                    // Permission already granted:
                    enabled(true);
                } else if (shouldShowRequestPermissionRationale(perm)) {
                    enabled(false);
                    // Initializing:
                    String mes = "We Cannot Start The App Without This Permissions";
                    Snackbar snackbar = Snackbar.make(binding.getRoot(), mes, Snackbar.LENGTH_INDEFINITE);
                    // Developing:
                    snackbar.setAction("ACCESS", view -> launcher.launch(perm));
                    snackbar.show();
                } else {
                    enabled(false);
                    // Permission not granted so we have to ask it:
                    launcher.launch(perm);
                }
            } else if (position == 2)
                OnBoardFragment.this.request.runNotificationAccess();
            // Super:
            super.onPageSelected(position);
        }

        // PermissionListener:
        private void permResult(boolean isGranted) {
            // Initializing:
            enabled(isGranted);
            // Developing:
            if (!isGranted) launcher.launch(perm);
        }

        // Disable&EnableMethod:
        private void enabled(boolean state) {
            binding.nextBoardLayout.setEnabled(state);
            binding.onBoardPager.setEnabled(state);
        }
    }

    // Moving to next page (ButtonMethod):
    private void moveToNextPage(View view) {
        if (index < textViews.length) {
            index++;
            binding.onBoardPager.setCurrentItem(index);
        } else {
            manager.setBooleanPreferences(ARPreferencesManager.APP_INIT, true);
            startActivity(ARUtils.runNewTask(requireContext(), requireActivity(), HomeActivity.class));
        }
    }

    // InitializingPager:
    private void initPager() {
        // Initializing:
        boards = new ArrayList<>();
        // AddingContent:
        boards.add(getBoard(R.drawable.attention, R.string.page_1_header, R.string.page_1_content));
        boards.add(getBoard(R.drawable.ic_privacy_guard, R.string.page_2_header, R.string.page_2_content));
        boards.add(getBoard(R.drawable.ready_to_go, R.string.page_3_header, R.string.page_3_content));
        // Initializing(Adapter & Texts):
        textViews = new TextView[boards.size()];
        adapter = new BoardAdapter(requireContext(), boards);
        // Dots:
        for (int i = 0; i < boards.size(); i++) {
            // Initializing:
            textViews[i] = new TextView(requireContext());
            // SettingAttrs:
            textViews[i].setText(Html.fromHtml("&#9679;"));
            textViews[i].setTextSize(30);
            textViews[i].setTextColor(Color.GRAY);
            textViews[i].setPadding(8, 0, 8, 0);
            // Finishing:
            binding.dotsLayout.addView(textViews[i]);
        }
    }

    // GetBoardsFunctions:
    private Board getBoard(@DrawableRes int res, @StringRes int headerRes, @StringRes int contentRes) {
        // Initializing:
        Drawable drawable = ContextCompat.getDrawable(requireContext(), res);
        String header = getString(headerRes);
        String content = getString(contentRes);
        // Developing:
        return new Board(drawable, header, content);
    }
}