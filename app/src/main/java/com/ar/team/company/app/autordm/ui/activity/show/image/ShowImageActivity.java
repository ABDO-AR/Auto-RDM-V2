package com.ar.team.company.app.autordm.ui.activity.show.image;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.ar.team.company.app.autordm.control.adapter.ImagesAdapter;
import com.ar.team.company.app.autordm.control.adapter.SliderImageAdapter;
import com.ar.team.company.app.autordm.control.adapter.StatusAdapter;
import com.ar.team.company.app.autordm.databinding.ActivityShowImageBinding;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ShowImageActivity extends AppCompatActivity {

    // This For Control The XML-Main Views:
    private ActivityShowImageBinding binding;
    // Adapter:
    private SliderImageAdapter adapter;
    private List<Bitmap> bitmaps;
    private int index;
    // TAGS:
    private static final String TAG = "ShowImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowImageBinding.inflate(getLayoutInflater()); // INFLATE THE LAYOUT.
        View view = binding.getRoot(); // GET ROOT [BY DEF(CONSTRAINT LAYOUT)].
        setContentView(view); // SET THE VIEW CONTENT TO THE (VIEW).
        // Initializing:
        index = getIntent().getExtras().getInt("Index");
        if (getIntent().getExtras().getString("TAG").equals("Images")) bitmaps = ImagesAdapter.staticBitmaps;
        else bitmaps = StatusAdapter.staticBitmaps;
        adapter = new SliderImageAdapter(this, bitmaps);
        // PreparingTextView:
        binding.imagesSizeTextView.setText((index + 1) + "/" + bitmaps.size());
        // PreparingViewPager:
        binding.imagesViewPager.setAdapter(adapter);
        binding.imagesViewPager.setCurrentItem(index);
        // PreparingViewPager(Callbacks):
        binding.imagesViewPager.registerOnPageChangeCallback(new ARViewPagerCallbacks());
        // Developing:
        binding.backButton.setOnClickListener(v -> finish());
    }

    // ViewPagerCallbacks:
    class ARViewPagerCallbacks extends ViewPager2.OnPageChangeCallback {
        // Methods(OnPageSelected):
        @Override
        public void onPageSelected(int position) {
            binding.imagesSizeTextView.setText((position + 1) + "/" + bitmaps.size());
            binding.shareButton.setOnClickListener(view1 -> sharePalette(bitmaps.get(position)));
            super.onPageSelected(position);
        }
    }

    // SharingBitmaps:
    private void sharePalette(Bitmap bitmap) {
        // Initializing:
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "palette", "share palette");
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        // Preparing:
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        // Developing:
        startActivity(Intent.createChooser(intent, "Share"));
    }
}