package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.databinding.VoiceItemViewBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class VoicesAdapter extends RecyclerView.Adapter<VoicesAdapter.VoicesViewHolder> {

    // Fields:
    private final Context context;
    private final List<File> files;
    // Fields(MediaPlayer):
    private int index = 9999999;
    private MediaPlayer player;
    // TAGS:
    private static final String TAG = "VoicesAdapter";

    // Constructor:
    public VoicesAdapter(Context context, List<File> files) {
        this.context = context;
        this.files = files;
        this.player = new MediaPlayer();
        notifyDataSetChanged();
    }

    // Adapter:
    @NonNull
    @NotNull
    @Override
    public VoicesViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Initializing:
        LayoutInflater inflater = LayoutInflater.from(context);
        VoiceItemViewBinding binding = VoiceItemViewBinding.inflate(inflater, parent, false);
        // Returning:
        return new VoicesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VoicesAdapter.VoicesViewHolder holder, int position) {
        // Initializing:
        File file = files.get(position);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa", Locale.US);
        // Initializing(MediaMetadataRetriever):
        try {
            retriever.setDataSource(context, Uri.parse(file.getAbsolutePath()));
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: " + e.toString());
        }
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // Checking:
        if (position == index)
            holder.binding.playVoiceButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause));
        else
            holder.binding.playVoiceButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play));
        // Developing:
        holder.binding.durationTextView.setText(formatsMilliSeconds(Long.parseLong(duration)));
        holder.binding.dateTextView.setText(format.format(file.lastModified()));
        holder.binding.playVoiceButton.setOnClickListener(v -> audioMethod(holder.binding, position));
    }

    // Method(AUDIO):
    private void audioMethod(VoiceItemViewBinding binding, int position) {
        // Debugging:
        Log.d(TAG, "audioMethod: --------------------------------------------------");
        Log.d(TAG, "audioMethod: AudioMethod Called With Position(" + position + ")");
        // Initializing:
        String audioPath = files.get(position).getAbsolutePath();
        // Checking:
        if (!player.isPlaying()) {
            // IT'S NOT PLAYING:
            try {
                // Initializing:
                player.setDataSource(audioPath);
                player.prepare();
                player.start();
                // Debugging:
                Log.d(TAG, "audioMethod: StartPlaying Audio");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // SettingRes:
            binding.playVoiceButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause));
        } else {
            // IT'S ALREADY PLAYING:
            try {
                // Stopping:
                player.stop();
                // Initializing:
                player = new MediaPlayer();
                // SettingRes:
                binding.playVoiceButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play));
                // Debugging:
                Log.d(TAG, "audioMethod: StopPlaying Audio");
                // Checking:
                if (index != position) audioMethod(binding, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Setting:
        player.setOnCompletionListener(mediaPlayer -> onPlayerComplete(mediaPlayer, binding, position));
        // Setting:
        index = position;
        // Notify:
        notifyDataSetChanged();
        // Checking:
        if (index != position) audioMethod(binding, position);
        // Debugging:
        Log.d(TAG, "audioMethod: EndingOfMethod");
        Log.d(TAG, "audioMethod: --------------------------------------------------");
    }

    // Method(OnPlayerCompleted):
    private void onPlayerComplete(MediaPlayer parentPlayer, VoiceItemViewBinding binding, int position) {
        // Checking:
        if (player.isPlaying()) player.stop();
        // Initializing:
        player = new MediaPlayer();
        // Developing:
        binding.playVoiceButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play));
    }

    // Formatting:
    public static String formatsMilliSeconds(long milliseconds) {
        // Initializing:
        String finalTimerString = "";
        String secondsString;
        // Convert total duration into time:
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there:
        if (hours > 0) finalTimerString = hours + ":";
        // Prepending 0 to seconds if it is one digit:
        if (seconds < 10) secondsString = "0" + seconds;
        else secondsString = "" + seconds;
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        // return timer string:
        return finalTimerString;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    // Holder:
    @SuppressWarnings("unused")
    static class VoicesViewHolder extends RecyclerView.ViewHolder {

        // Fields:
        private final VoiceItemViewBinding binding;

        public VoicesViewHolder(@NonNull @NotNull VoiceItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public VoiceItemViewBinding getBinding() {
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

    public MediaPlayer getPlayer() {
        return player;
    }
}
