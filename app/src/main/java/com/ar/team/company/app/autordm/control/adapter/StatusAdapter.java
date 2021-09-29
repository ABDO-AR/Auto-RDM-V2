package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.ar.images.ARImagesAccess;
import com.ar.team.company.app.autordm.databinding.ImageViewItemBinding;
import com.ar.team.company.app.autordm.databinding.VideoItemViewBinding;
import com.ar.team.company.app.autordm.ui.activity.show.image.ShowImageActivity;
import com.ar.team.company.app.autordm.ui.activity.show.video.ShowVideoActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter {

    // Fields:
    private final Context context;
    private final List<File> files;
    public static List<Bitmap> staticBitmaps = new ArrayList<>();
    // ViewTypes:
    public static final int VIEW_TYPE_IMAGE = 0;
    public static final int VIEW_TYPE_VIDEO = 1;

    // Constructor:
    public StatusAdapter(Context context, List<File> files) {
        // Initializing:
        this.context = context;
        this.files = files;
        // Notify:
        notifyDataSetChanged();
    }

    // Adapter:
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Initializing:
        LayoutInflater inflater = LayoutInflater.from(context);
        // Checking(Returning):
        if (viewType == VIEW_TYPE_IMAGE) {
            // Initializing:
            ImageViewItemBinding binding = ImageViewItemBinding.inflate(inflater, parent, false);
            // Returning:
            return new StatusImageViewHolder(binding);
        } else {
            // Initializing:
            VideoItemViewBinding binding = VideoItemViewBinding.inflate(inflater, parent, false);
            // Returning:
            return new StatusVideoViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        // Initializing:
        File file = files.get(position);
        // Checking:
        if (holder.getItemViewType() == VIEW_TYPE_IMAGE) {
            // Initializing:
            Bitmap bitmap = ARImagesAccess.ARBitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(), 800, 800);
            // Developing:
            ((StatusImageViewHolder) holder).binding.imageViewItem.setImageBitmap(bitmap);
            ((StatusImageViewHolder) holder).binding.imageViewItem.setOnClickListener(view -> slidingImage(position));
        } else {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            // Developing:
            ((StatusVideoViewHolder) holder).binding.videoThumbnail.setImageBitmap(thumb);
            ((StatusVideoViewHolder) holder).binding.playVideoButton.setOnClickListener(v -> playVideo(file));
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Initializing:
        boolean image = files.get(position).getAbsolutePath().endsWith(".png") || files.get(position).getAbsolutePath().endsWith(".jpg");
        // Checking(Returning):
        if (image) return VIEW_TYPE_IMAGE;
        else return VIEW_TYPE_VIDEO;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    // Methods:
    private void slidingImage(int pos) {
        // Initializing:
        Intent intent = new Intent(context, ShowImageActivity.class);
        int realPos = 0;
        List<File> bitmapsFiles = new ArrayList<>();
        List<Bitmap> bitmaps = new ArrayList<>();
        // Clearing:
        if (!staticBitmaps.isEmpty()) staticBitmaps.clear();
        // Looping:
        for (File file : files) {
            // Checking:
            if (!file.getAbsolutePath().endsWith(".mp4")) {
                // Setting:
                bitmapsFiles.add(file);
                bitmaps.add(ARImagesAccess.ARBitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(), 800, 800));
            }
        }
        // AddingStatic:
        staticBitmaps = bitmaps;
        // Looping:
        for (int index = 0; index < bitmaps.size(); index++) {
            // Checking:
            if (bitmapsFiles.get(index).getName().equals(files.get(pos).getName())) realPos = index;
        }
        // PuttingExtras:
        intent.putExtra("Index", realPos);
        intent.putExtra("TAG", "Status");
        // Developing:
        context.startActivity(intent);
    }

    // PlayingVideos:
    private void playVideo(File file) {
        // Checking:
        if (file.getAbsolutePath().endsWith(".mp4")) {
            // Initializing:
            Intent intent = new Intent(context, ShowVideoActivity.class);
            // PuttingExtras:
            intent.putExtra("Uri", file.getAbsolutePath());
            // Developing:
            context.startActivity(intent);
        }
    }

    // Holders:
    static class StatusImageViewHolder extends RecyclerView.ViewHolder {
        // Fields:
        private final ImageViewItemBinding binding;

        // Constructor:
        public StatusImageViewHolder(@NonNull @NotNull ImageViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public ImageViewItemBinding getBinding() {
            return binding;
        }
    }

    static class StatusVideoViewHolder extends RecyclerView.ViewHolder {
        // Fields:
        private final VideoItemViewBinding binding;

        // Constructor:
        public StatusVideoViewHolder(@NonNull @NotNull VideoItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public VideoItemViewBinding getBinding() {
            return binding;
        }
    }

    // Getters:
    public Context getContext() {
        return context;
    }

    public List<File> getFiles() {
        return files;
    }
}
