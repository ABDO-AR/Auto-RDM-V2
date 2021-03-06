package com.ar.team.company.app.autordm.control.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.ar.images.ARImagesAccess;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.databinding.DocumentsItemViewBinding;
import com.ar.team.company.app.autordm.databinding.ImageViewItemBinding;
import com.ar.team.company.app.autordm.databinding.VideoItemViewBinding;
import com.ar.team.company.app.autordm.databinding.VoiceItemViewBinding;
import com.ar.team.company.app.autordm.model.ARMedia;
import com.ar.team.company.app.autordm.model.Document;
import com.ar.team.company.app.autordm.ui.activity.show.image.ShowImageActivity;
import com.ar.team.company.app.autordm.ui.activity.show.video.ShowVideoActivity;


import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Fields:
    private final Context context;
    private final ARMedia media;
    private final ARPreferencesManager manager;
    // Fields(MediaPlayer):
    private MediaPlayer player;
    private Handler seekHandler = new Handler();
    private Runnable run;
    // Fields(Temp):
    private int index = 9999999;
    // Static(BitMaps):
    public static List<Bitmap> staticBitmaps = new ArrayList<>();
    // ViewTypes:
    private static final int IMAGES_VIEW_TYPE = 0;
    private static final int VIDEOS_VIEW_TYPE = 1;
    private static final int VOICES_VIEW_TYPE = 2;
    private static final int DOCUMENTS_VIEW_TYPE = 3;
    // TAGS:
    public static final String TAG = "MediaAdapter";

    // Constructor:
    @SuppressLint("NotifyDataSetChanged")
    public MediaAdapter(Context context, ARMedia media) {
        // Initializing:
        this.context = context;
        this.media = media;
        this.manager = new ARPreferencesManager(context);
        this.player = new MediaPlayer();
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
        // Checking(IF):
        if (viewType == IMAGES_VIEW_TYPE) {
            // Initializing:
            ImageViewItemBinding binding = ImageViewItemBinding.inflate(inflater, parent, false);
            // Returning:
            return new ImagesViewHolder(binding);
            // Checking(ELSE-IF):
        } else if (viewType == VIDEOS_VIEW_TYPE) {
            // Initializing:
            VideoItemViewBinding binding = VideoItemViewBinding.inflate(inflater, parent, false);
            // Returning:
            return new VideosViewHolder(binding);
            // Checking(ELSE-IF):
        } else if (viewType == VOICES_VIEW_TYPE) {
            // Initializing:
            VoiceItemViewBinding binding = VoiceItemViewBinding.inflate(inflater, parent, false);
            // Returning:
            return new VoicesViewHolder(binding);
            // Checking(ELSE):
        } else {
            // Initializing:
            DocumentsItemViewBinding binding = DocumentsItemViewBinding.inflate(inflater, parent, false);
            // Returning:
            return new DocumentViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        // Initializing:
        File file = media.getContent().get(position);
        // Checking:
        if (holder.getItemViewType() == IMAGES_VIEW_TYPE) {
            // Initializing:
            ImagesViewHolder imagesHolder = (ImagesViewHolder) holder;
            Bitmap bitmap = ARImagesAccess.ARBitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(), 800, 800);
            // Developing:
            imagesHolder.binding.imageViewItem.setImageBitmap(bitmap);
            imagesHolder.binding.imageViewItem.setOnClickListener(view -> slidingImage(position));
            imagesHolder.binding.shareButton.setOnClickListener(view1 -> shareImage(bitmap, file));
            imagesHolder.binding.saveButton.setOnClickListener(view1 -> saveImage(bitmap, file));

        } else if (holder.getItemViewType() == VIDEOS_VIEW_TYPE) {
            // Initializing:
            VideosViewHolder videosHolder = (VideosViewHolder) holder;
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            // Developing:
            videosHolder.binding.videoThumbnail.setImageBitmap(thumb);
            videosHolder.binding.playVideoButton.setOnClickListener(v -> playVideo(file));
            videosHolder.binding.shareButton.setOnClickListener(view1 -> shareVideo(file));
            videosHolder.binding.saveButton.setOnClickListener(view1 -> saveVideo(file.getAbsolutePath(), file));
        } else if (holder.getItemViewType() == VOICES_VIEW_TYPE) {
            // Initializing:
            VoicesViewHolder voicesHolder = (VoicesViewHolder) holder;
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
                voicesHolder.binding.playVoiceButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause));
            else
                voicesHolder.binding.playVoiceButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play));
            // Developing:
            voicesHolder.binding.shareVoiceButton.setOnClickListener(v -> onVoiceShare(file, position));
            voicesHolder.binding.durationTextView.setText(formatsMilliSeconds(Long.parseLong(duration)));
            voicesHolder.binding.dateTextView.setText(format.format(file.lastModified()));
            voicesHolder.binding.playVoiceButton.setOnClickListener(v -> audioMethod(voicesHolder.binding, position));
        } else {
            // Initializing:
            DocumentViewHolder documentsHolder = (DocumentViewHolder) holder;
            String fileSize = getFileSize(file);
            Document document;
            // Adding:
            if (file.getAbsolutePath().endsWith(".pdf")) {
                // Adding:
                document = new Document(file.getName(), fileSize, "PDF File", ContextCompat.getDrawable(context, R.drawable.folder_blue_icon), ContextCompat.getColor(context, R.color.folder_blue));
            } else if (file.getAbsolutePath().endsWith(".txt")) {
                // Adding:
                document = new Document(file.getName(), fileSize, "DOC File", ContextCompat.getDrawable(context, R.drawable.folder_orange_icon), ContextCompat.getColor(context, R.color.folder_orange));
            } else if (file.getAbsolutePath().endsWith(".rar")) {
                // Adding:
                document = new Document(file.getName(), fileSize, "RAR File", ContextCompat.getDrawable(context, R.drawable.folder_purple_icon), ContextCompat.getColor(context, R.color.folder_purple));
            } else if (file.getAbsolutePath().endsWith(".apk")) {
                // Adding:
                document = new Document(file.getName(), fileSize, "APK File", ContextCompat.getDrawable(context, R.drawable.folder_red_icon), ContextCompat.getColor(context, R.color.folder_red));
            } else if (file.getAbsolutePath().endsWith(".zip")) {
                // Adding:
                document = new Document(file.getName(), fileSize, "ZIP File", ContextCompat.getDrawable(context, R.drawable.folder_green_icon), ContextCompat.getColor(context, R.color.folder_green));
            } else {
                // Adding:
                document = new Document(file.getName(), fileSize, "OTHER File", ContextCompat.getDrawable(context, R.drawable.folder_pink_icon), ContextCompat.getColor(context, R.color.folder_pink));
            }
            // Developing:
            documentsHolder.binding.folderImageView.setImageDrawable(document.getDocIcon());
            documentsHolder.binding.fileExtensionTextView.setText(document.getDocType());
            documentsHolder.binding.fileSizeTextView.setText(document.getDocSize());
            documentsHolder.binding.lastFileNameTextView.setText(document.getLastDocName());
            documentsHolder.binding.fileExtensionTextView.setTextColor(document.getDocColor());
        }
    }

    private void onVoiceShare(File file, int position) {
        // Checking:
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            String sharePath = file.getAbsolutePath();
            Uri uri = Uri.parse(sharePath);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(share, "Share Sound File"));
        }
    }


    @Override
    public int getItemViewType(int position) {
        // Initializing:
        int viewType;
        // Checking(IF):
        if (getMediaContentType(position, ".jpg") || getMediaContentType(position, ".png"))
            viewType = IMAGES_VIEW_TYPE;
            // Checking(ELSE-IF):
        else if (getMediaContentType(position, ".mp4")) viewType = VIDEOS_VIEW_TYPE;
        else if (getMediaContentType(position, ".opus")) viewType = VOICES_VIEW_TYPE;
            // Checking(ELSE):
        else viewType = DOCUMENTS_VIEW_TYPE;
        // Returning:
        return viewType;
    }

    @Override
    public int getItemCount() {
        // Returning:
        return media.getContent().size();
    }

    // Methods:
    private boolean getMediaContentType(int position, String extension) {
        // Returning:
        return media.getContent().get(position).getAbsolutePath().endsWith(extension);
    }

    // RunningSlidingImage:
    private void slidingImage(int pos) {
        // Initializing:
        Intent intent = new Intent(context, ShowImageActivity.class);
        // Initializing(Bitmaps):
        List<File> bitmapsFiles = new ArrayList<>();
        List<Bitmap> bitmaps = new ArrayList<>();
        // Initializing(RealPosition):
        int realPos = 0;
        // Clearing:
        if (!staticBitmaps.isEmpty()) staticBitmaps.clear();
        // Looping:
        for (File file : media.getContent()) {
            // Checking:
            if (!file.getAbsolutePath().endsWith(".mp4") && !file.getAbsolutePath().endsWith(".opus")) {
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
            if (bitmapsFiles.get(index).getName().equals(media.getContent().get(pos).getName()))
                realPos = index;
        }
        // PuttingExtras:
        intent.putExtra("Index", realPos);
        intent.putExtra("TAG", "Images");
        // Developing:
        context.startActivity(intent);
    }

    // PlayingVideos:
    private void playVideo(File file) {
        // Initializing:
        Intent intent = new Intent(context, ShowVideoActivity.class);
        // PuttingExtras:
        intent.putExtra("Uri", file.getAbsolutePath());
        // Developing:
        context.startActivity(intent);
    }

    // Method(AUDIO):
    private void audioMethod(VoiceItemViewBinding binding, int position) {
        // Debugging:
        Log.d(TAG, "audioMethod: --------------------------------------------------");
        Log.d(TAG, "audioMethod: AudioMethod Called With Position(" + position + ")");
        // Initializing:
        String audioPath = media.getContent().get(position).getAbsolutePath();
        // Checking:
        if (!player.isPlaying()) {
            // IT'S NOT PLAYING:
            try {
                // Initializing:
                player.setDataSource(audioPath);
                player.prepare();
                player.start();
                // StartSeeking:
                binding.seekBar.setMax(player.getDuration());
                binding.seekBar.setTag(position);
                binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (player != null && b) {
                            player.seekTo(i);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                run = () -> {
                    if (player != null && player.isPlaying()) {
                        // Updateing SeekBar every 100 miliseconds
                        binding.seekBar.setProgress(player.getCurrentPosition());
                        seekHandler.postDelayed(run, 100);
                        //For Showing time of audio(inside runnable)
                        int miliSeconds = player.getCurrentPosition();
                        if (miliSeconds != 0) {
                            //if audio is playing, showing current time;
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds);
                            if (minutes == 0) {
                                binding.durationTextView.setText("0:" + seconds + "/" + calculateDuration(player.getDuration()));
                            } else {
                                if (seconds >= 60) {
                                    long sec = seconds - (minutes * 60);
                                    binding.durationTextView.setText(minutes + ":" + sec + "/" + calculateDuration(player.getDuration()));
                                }
                            }
                        } else {
                            //Displaying total time if audio not playing
                            int totalTime = player.getDuration();
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime);
                            if (minutes == 0) {
                                binding.durationTextView.setText("0:" + seconds);
                            } else {
                                if (seconds >= 60) {
                                    long sec = seconds - (minutes * 60);
                                    binding.durationTextView.setText(minutes + ":" + sec);
                                }
                            }
                        }
                    }
                };
                run.run();
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
                // StopSeeking:
                binding.seekBar.setProgress(0);
                binding.seekBar.setOnSeekBarChangeListener(null);
                // Debugging:
                Log.d(TAG, "audioMethod: StopPlaying Audio");
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
        // Debugging:
        Log.d(TAG, "audioMethod: EndingOfMethod");
        Log.d(TAG, "audioMethod: --------------------------------------------------");
    }

    private String calculateDuration(int duration) {
        String finalDuration = "";
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        if (minutes == 0) {
            finalDuration = "0:" + seconds;
        } else {
            if (seconds >= 60) {
                long sec = seconds - (minutes * 60);
                finalDuration = minutes + ":" + sec;
            }
        }
        return finalDuration;
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

    public static String getFileSize(File file) {
        // Initializing:
        String fileSize;
        long kb = (file.length() / 1024);
        // Checking:
        if (kb > 1000) fileSize = (kb / 1024) + " MB";
        else fileSize = kb + " KB";
        // Returning:
        return fileSize;
    }

    public static File[] getDocumentsFiles() {
        // Initializing(Paths):
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        String whatsappImagesPath = "/WhatsApp/Media/WhatsApp Documents";
        String finalPath = externalStorageDirectory + whatsappImagesPath;
        // Initializing(Paths2):
        String whatsappImagesPath2 = "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Documents";
        String finalPath2 = externalStorageDirectory + whatsappImagesPath2;
        // FieldsField:
        File[] backupFiles = new File(finalPath2).listFiles();
        File[] files = new File(finalPath).listFiles();
        // Checking:
        if (files == null || files.length <= 0) files = backupFiles;
        // Returning:
        return files;
    }

    // ViewHolders:
    @SuppressWarnings("unused")
    static class ImagesViewHolder extends RecyclerView.ViewHolder {
        // Fields:
        private final ImageViewItemBinding binding;

        // Constructor:
        public ImagesViewHolder(@NonNull @NotNull ImageViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public ImageViewItemBinding getBinding() {
            return binding;
        }
    }

    @SuppressWarnings("unused")
    static class VideosViewHolder extends RecyclerView.ViewHolder {
        // Fields:
        private final VideoItemViewBinding binding;

        // Constructor:
        public VideosViewHolder(@NonNull @NotNull VideoItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public VideoItemViewBinding getBinding() {
            return binding;
        }
    }

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

    @SuppressWarnings("unused")
    static class DocumentViewHolder extends RecyclerView.ViewHolder {

        // Fields:
        private final DocumentsItemViewBinding binding;

        // Constructor:
        public DocumentViewHolder(@NonNull @NotNull DocumentsItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public DocumentsItemViewBinding getBinding() {
            return binding;
        }
    }

    // Getters:
    public Context getContext() {
        return context;
    }

    public ARMedia getMedia() {
        return media;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    // SharingBitmaps:
    private void shareImage(Bitmap bitmap, File file) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 55);
            return;
        }

        Toast.makeText(context, context.getString(R.string.wait), Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            Intent intent = new Intent();

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name) + " Sharing ...");
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            Uri imageUri = Uri.parse(file.getAbsolutePath());
            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(imageUri);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            context.startActivity(Intent.createChooser(intent, "Share..."));
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name) + " Sharing ...");
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            Uri imageUri = Uri.parse(file.getAbsolutePath());
            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(imageUri);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            context.startActivity(Intent.createChooser(intent, "Share..."));
        }
    }

    @SuppressLint("CheckResult")
    private void saveImage(Bitmap bitmap, File file) {
        OutputStream outputStream = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  // check android 10 or more
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_" + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name));
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Objects.requireNonNull(outputStream);
                Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Image Not  Saved: \n " + e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                Objects.requireNonNull(outputStream).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 55);
                return;
            }

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 55);
                return;
            }
            File filePath = Environment.getExternalStorageDirectory();
            File dir = new File(filePath.getAbsolutePath() + "/" + context.getString(R.string.app_name) + "/");
            dir.mkdir();
            File file1 = new File(dir, System.currentTimeMillis() + ".jpg");
            try {
                outputStream = new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "Image Not   Saved: \n " + e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // SharingBitmaps:
    private void shareVideo(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        } else {
            Toast.makeText(context, context.getString(R.string.wait), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            ArrayList<Uri> uris = new ArrayList<>();
            Uri videoUri = Uri.fromFile(new File(file.getAbsolutePath()));
            uris.add(videoUri);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name) + file.getAbsolutePath());
            intent.putExtra(Intent.EXTRA_TITLE, "Video from" + context.getString(R.string.app_name));
            context.startActivity(Intent.createChooser(intent, "Share.."));
        }
    }

    @SuppressLint("CheckResult")
    private void saveVideo(String localPath, File file2) {
        Toast.makeText(context, "lo: " + localPath, Toast.LENGTH_LONG).show();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 55);
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 55);
            return;
        }

        //OutputStream outputStream;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            //ContentResolver resolver = context.getContentResolver();
            //ContentValues contentValues = new ContentValues();
            //contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Video_" + ".mp4");
            //contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            //contentValues.put(MediaStore.Video.Media.DATA, localPath);
            //contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name));
            //Uri imageUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            //try {
            //    outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            //    //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //    Objects.requireNonNull(outputStream);
            //    Toast.makeText(context, "Video Saved", Toast.LENGTH_SHORT).show();
            //} catch (Exception e) {
            //    Toast.makeText(context, "Video Not  Saved: \n " + e, Toast.LENGTH_SHORT).show();
            //    e.printStackTrace();
            //}
            File filePath = Environment.getExternalStorageDirectory();
            File dir = new File(filePath.getAbsolutePath() + "/" + context.getString(R.string.app_name) + "/");
            if (!dir.exists()) {
                boolean state = dir.mkdir();
                if (!state) {
                    try {
                        Files.createDirectory(Paths.get(dir.getAbsolutePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            ARAccess.copy(file2, new File(dir.getAbsolutePath() + "/" + file2.getName()));
        } else {
            File filePath = Environment.getExternalStorageDirectory();
            File dir = new File(filePath.getAbsolutePath() + "/" + context.getString(R.string.app_name) + "/");
            if (!dir.exists()) dir.mkdir();
            ARAccess.copy(file2, new File(dir.getAbsolutePath() + "/" + file2.getName()));
            /*       File dir = new File(filePath.getAbsolutePath() + "/" + context.getString(R.string.app_name)+ "/" );

            dir.mkdir();

            File file1 = new File(dir, System.currentTimeMillis() + ".mp4");

            try {
                outputStream = new FileOutputStream(file1);


                Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "Image Not   Saved: \n " + e, Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }


            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
        }
    }

    // Check if file exists
    public static boolean isFileExists(String path) {
        if (path == null) return false;
        return new File(path).exists();
    }
}
