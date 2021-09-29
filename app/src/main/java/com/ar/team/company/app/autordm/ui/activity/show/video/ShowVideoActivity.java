package com.ar.team.company.app.autordm.ui.activity.show.video;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import com.ar.team.company.app.autordm.databinding.ActivityShowVideoBinding;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ShowVideoActivity extends AppCompatActivity {

    // This For Control The XML-Main Views:
    private ActivityShowVideoBinding binding;
    // VideoFields:
    private Uri uri;
    private MediaController controller;
    // TAGS:
    private static final String TAG = "ShowVideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowVideoBinding.inflate(getLayoutInflater()); // INFLATE THE LAYOUT.
        View view = binding.getRoot(); // GET ROOT [BY DEF(CONSTRAINT LAYOUT)].
        setContentView(view); // SET THE VIEW CONTENT TO THE (VIEW).
        // Initializing:
        uri = Uri.parse(getIntent().getExtras().getString("Uri"));
        controller = new MediaController(this);
        // PreparingVideo:
        binding.videoView.setVideoURI(uri);
        binding.videoView.setMediaController(controller);
        // PreparingControl:
        controller.requestFocus();
        controller.setAnchorView(binding.videoView);
        // Starting:
        binding.videoView.start();
    }
}