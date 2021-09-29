package com.ar.team.company.app.autordm.ui.fragment.splash;

import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.databinding.FragmentSplashBinding;
import com.ar.team.company.app.autordm.ui.activity.home.HomeActivity;
import com.ar.team.company.app.autordm.ui.fragment.onboard.OnBoardFragment;
import com.ar.team.company.app.autordm.ar.utils.ARUtils;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldCanBeLocal")
public class SplashFragment extends Fragment implements Animator.AnimatorListener {

    // This for control the Fragment-Layout views:
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private FragmentSplashBinding binding;
    // Preferences:
    private ARPreferencesManager manager;
    // TAGS:
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static final String TAG = "SplashFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout:
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Get the fragment layout root.
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing:
        manager = new ARPreferencesManager(requireContext());
        // Developing:
        binding.progressLoading.addAnimatorListener(this);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        // DefState:
        boolean state = manager.getBooleanPreferences(ARPreferencesManager.APP_INIT);
        // Checking:
        if (state) {
            startActivity(ARUtils.runNewTask(requireContext(), requireActivity(), HomeActivity.class));
        } else {
            // Initializing:
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            // Developing:
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.ar_container, new OnBoardFragment());
            transaction.commit();
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}