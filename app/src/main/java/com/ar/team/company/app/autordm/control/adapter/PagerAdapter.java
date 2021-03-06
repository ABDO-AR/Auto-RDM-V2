package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ui.fragment.home.ChatFragment;
import com.ar.team.company.app.autordm.ui.fragment.home.MediaFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PagerAdapter extends FragmentStateAdapter {

    // Fields:
    private final Context context;
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> headers = new ArrayList<>();

    // Constructor:
    public PagerAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle,Context context) {
        super(fragmentManager, lifecycle);
        this.context = context;
        // Initializing:
        initData();
    }

    // MainMethods:
    private void initData() {
        // AddingFragments(Content):
        addData(new ChatFragment(), context.getString(R.string.chat_fragment_name));
        addData(new MediaFragment(), context.getString(R.string.media_fragment_name));
        //addData(new ImagesFragment(), "Images");
        //addData(new VideosFragment(), "Videos");
        //addData(new VoiceFragment(), "Voice");
        //addData(new DocumentFragment(), "Document");
    }

    // AddDataMethod:
    private void addData(Fragment fragment, String header) {
        // AddingFragment:
        fragments.add(fragment);
        // AddingHeader:
        headers.add(header);
    }

    // FragmentStateAdapter:
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    // Getters:
    public List<Fragment> getFragments() {
        return fragments;
    }

    public String getHeaders(int pos) {
        return headers.get(pos);
    }
}
