package com.ar.team.company.app.autordm.ui.activity.home;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.PowerManager;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.ar.observer.ARFilesObserver;

import com.ar.team.company.app.autordm.ar.permissions.ARPermissionsRequest;
import com.ar.team.company.app.autordm.control.adapter.PagerAdapter;
import com.ar.team.company.app.autordm.control.notifications.NotificationListener;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;

import com.ar.team.company.app.autordm.databinding.ActivityHomeBinding;


import com.ar.team.company.app.autordm.ui.interfaces.ChatListener;
import com.ar.team.company.app.autordm.ui.interfaces.HomeItemClickListener;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import java.io.File;
import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class HomeActivity extends AppCompatActivity implements HomeItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener, ChatListener {

    // This For Control The XML-Main Views:
    private ActivityHomeBinding binding;
    private HomeViewModel model;
    // Drawer(&TabLayout):
    private PagerAdapter adapter;
    private ARPreferencesManager manager;
    // TabMediator:
    private TabLayoutMediator mediator;
    // Dialogs:
    private ProgressDialog dialog;
    // WhatsAppDirsObservers:
    private static FileObserver imagesObserver;
    private static FileObserver videosObserver;
    private static FileObserver voicesObserver;
    private static FileObserver documentsObserver;
    // Permissions:
    private static final String requestPermName = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final ActivityResultContracts.RequestPermission request = new ActivityResultContracts.RequestPermission();
    // ARPermissionsRequest:
    private ARPermissionsRequest arPermissionsRequest;
    private ActivityResultLauncher<String> launcher;
    // TempData:
    private Thread tempThread;
    // TAGS:
    private static final String TAG = "HomeActivity";
    private static final String perm = Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater()); // INFLATE THE LAYOUT.
        View view = binding.getRoot(); // GET ROOT [BY DEF(CONSTRAINT LAYOUT)].
        setContentView(view); // SET THE VIEW CONTENT TO THE (VIEW).
        // Initializing(MAIN-FIELDS):
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        manager = new ARPreferencesManager(this);
        // Initializing(PERM-FIELDS):
        launcher = registerForActivityResult(request, this::requestPermission);
        // Initializing(Access):
        initPermissionsAccess();
    }

    private void requestPermission(boolean isGranted) {
        // Checking:
        if (isGranted) {
            // Checking:
            importantPermissions();
            // Asking(A-11):
            android11StorageAccess();
            // Observers:
            initObservers();
        } else launcher.launch(requestPermName);
    }

    private void initPermissionsAccess() {
        // Checking:
        if (ContextCompat.checkSelfPermission(this, requestPermName) == PackageManager.PERMISSION_GRANTED) {
            // Checking:
            importantPermissions();
            // Observers:
            initObservers();
        } else if (shouldShowRequestPermissionRationale(requestPermName)) {
            // Initializing:
            String mes = "This app cannot work without this permission";
            Snackbar snackbar = Snackbar.make(binding.getRoot(), mes, Snackbar.LENGTH_INDEFINITE);
            // Preparing:
            snackbar.setAction("ACCESS", v -> launcher.launch(requestPermName));
            // Show:
            snackbar.show();
        } else {
            // Asking for the permissions:
            launcher.launch(requestPermName);
            // Asking(A-11):
            android11StorageAccess();
        }
    }

    private void importantPermissions() {
        // Developing:
        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted:
        } else if (shouldShowRequestPermissionRationale(perm)) {
            // Initializing:
            String mes = "We Cannot Start The App Without This Permissions";
            Snackbar snackbar = Snackbar.make(binding.getRoot(), mes, Snackbar.LENGTH_INDEFINITE);
            // Developing:
            snackbar.setAction("ACCESS", view -> launcher.launch(perm));
            snackbar.show();
        } else {
            // Permission not granted so we have to ask it:
            launcher.launch(perm);
        }
        arPermissionsRequest = new ARPermissionsRequest(this);
        arPermissionsRequest.runNotificationAccess();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    // Method(ANDROID-11):
    @SuppressLint("WrongConstant")
    public void android11StorageAccess() {
        // Checking:
        if (Build.VERSION.SDK_INT >= 30) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Setting Dialog Title
            builder.setTitle("Storage");
            // Setting Dialog Message
            builder.setMessage("We need your permission to start the app.");
            // Setting Positive "Yes" Btn
            builder.setPositiveButton("YES", (dialog, which) -> {
                // Initializing:
                StorageManager sm = (StorageManager) getSystemService(STORAGE_SERVICE);
                String str = "android.provider.extra.INITIAL_URI";
                String whatsAppStorageDir = getWhatsAppStorage();
                Intent intent;
                if (Build.VERSION.SDK_INT >= 30) {
                    // Initializing:
                    intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                    String scheme = intent.getParcelableExtra(str).toString().replace("/root/", "/document/");
                    // Preparing:
                    String stringBuilder = scheme + "%3A" + whatsAppStorageDir;
                    // Putting:
                    intent.putExtra(str, Uri.parse(stringBuilder));
                } else {
                    // Initializing:
                    intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                    // Putting:
                    intent.putExtra(str, Uri.parse(whatsAppStorageDir));
                }
                // Adding(Flags):
                intent.addFlags(2);
                intent.addFlags(1);
                intent.addFlags(128);
                intent.addFlags(64);
                // Starting:
                startActivity(intent);
            });
            // Preparing:
            builder.setCancelable(false);
            // Showing Alert Dialog
            builder.show();
        }
    }

    private String getWhatsAppStorage() {
        // Initializing:
        File storagePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media");
        // Checking:
        if (storagePath.exists()) return storagePath.getAbsolutePath();
        else
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media";
    }

    // Method(Observers):
    private void initObservers() {
        // Initializing(Dialog):
        dialog = new ProgressDialog(this);
        // Setting(Text):
        dialog.setTitle(getString(R.string.loading_dialog_name));
        dialog.setMessage(getString(R.string.loading_dialog_des));
        // ShowDialog:
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // Initializing(ImagesObserver):
        imagesObserver = new ARFilesObserver(this, ARAccess.WHATSAPP_IMAGES_PATH, model);
        // Initializing(VideosObserver):
        videosObserver = new ARFilesObserver(this, ARAccess.WHATSAPP_VIDEOS_PATH, model);
        // Initializing(VoicesObserver):
        voicesObserver = new ARFilesObserver(this, ARAccess.WHATSAPP_VOICES_PATH, model);
        // Initializing(DocumentsObserver):
        documentsObserver = new ARFilesObserver(this, ARAccess.WHATSAPP_DOCUMENTS_PATH, model);
        // Debugging:
        Log.d(TAG, "onEventCreate: " + ARAccess.WHATSAPP_IMAGES_PATH);
        Log.d(TAG, "onEventCreate: " + ARAccess.WHATSAPP_VIDEOS_PATH);
        Log.d(TAG, "onEventCreate: " + ARAccess.WHATSAPP_VOICES_PATH);
        Log.d(TAG, "onEventCreate: " + ARAccess.WHATSAPP_STATUS_PATH);
        Log.d(TAG, "onEventCreate: " + ARAccess.WHATSAPP_DOCUMENTS_PATH);
        // StartObservers:
        imagesObserver.startWatching();
        videosObserver.startWatching();
        voicesObserver.startWatching();
        documentsObserver.startWatching();
        // Preparing:
        tempThread = new Thread(this::preparingObservers);
        // Start:
        tempThread.start();
        // Initializing(App):
        initApp();
    }

    // Method(Preparing):
    private void preparingObservers() {
        // Initializing:
        boolean tempDirsState = manager.getBooleanPreferences(ARPreferencesManager.INIT_TEMP_DIR);
        // Checking:
        if (!tempDirsState) {
            // Setting:
            manager.setBooleanPreferences(ARPreferencesManager.INIT_TEMP_DIR, true);
            // StartInitializing:
            model.startMediaOperations();
        }
        // Hiding(Dialog):
        runOnUiThread(this::initUiThread);
        // Finishing:
        tempThread.interrupt();
    }

    private void initUiThread() {
        // Hiding(Dialog):
        dialog.hide();
        // Init(App):
        initApp();
    }

    // This method for control observer on ARImagesAccess:
    public static void setImagesObserver(boolean state) {
        // Checking:
        if (imagesObserver != null) {
            // Checking:
            if (state) imagesObserver.startWatching();
            else imagesObserver.stopWatching();
        }
    }

    public static void setVideosObserver(boolean state) {
        // Checking:
        if (videosObserver != null) {
            // Checking:
            if (state) videosObserver.startWatching();
            else videosObserver.stopWatching();
        }
    }

    public static void setVoicesObserver(boolean state) {
        // Checking:
        if (voicesObserver != null) {
            // Checking:
            if (state) voicesObserver.startWatching();
            else voicesObserver.stopWatching();
        }
    }

    public static void setDocumentsObserver(boolean state) {
        // Checking:
        if (documentsObserver != null) {
            // Checking:
            if (state) documentsObserver.startWatching();
            else documentsObserver.stopWatching();
        }
    }

    // InitApp:
    private void initApp() {
        // Initializing(MEDIATOR):
        mediator = new TabLayoutMediator(binding.mainContentLayout.homeTabLayout, binding.mainContentLayout.homeViewPager, (tab, position) -> tab.setText(adapter.getHeaders(position)));
        // Initializing(FIELDS):
        adapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle(), this);
        // AttachMediator:
        binding.mainContentLayout.homeViewPager.setAdapter(adapter);
        if (!mediator.isAttached()) mediator.attach();
        // Selecting:
        // openFragment(manager.getIntegerPreferences(ARPreferencesManager.FRAGMENT_STATE_NUMBER));
        // ManagerListener:
        manager.getPreferences().registerOnSharedPreferenceChangeListener(this);
        NotificationListener.listener = this;
    }

    // SharedPreferencesChangeListener:
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyName) {
        // Checking:
        if (keyName.equals(ARPreferencesManager.WHATSAPP_CHATS)) {
            // Initializing(APP):
            initApp();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onChatUpdate() {
        initApp();
    }

    @Override
    protected void onResume() {
        // Checking:
        //if (mediator != null){
        //    // Selecting:
        //    openFragment(manager.getIntegerPreferences(ARPreferencesManager.FRAGMENT_STATE_NUMBER));
        //}
        // Initializing(APP):
        //initApp();
        // Settings:
        NotificationListener.listener = this;
        // Hiding the dialog:
        if (dialog != null) dialog.hide();
        // Super:
        super.onResume();
    }

    @Override
    protected void onStop() {
        // Dismiss:
        dialog.dismiss();
        // Resetting:
        NotificationListener.listener = null;
        // UnRegistering:
        manager.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
        // Super:
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Initializing:
        boolean mediaState = model.getMediaThread() != null;
        // Checking(&Interrupting):
        if (mediaState) model.getMediaThread().interrupt();
        // Super:
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // InflatingHomeMenu:
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        // Returning:
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Developing:
        switch (item.getItemId()) {

            case R.id.menu_store:
                Toast.makeText(this, "Store", Toast.LENGTH_SHORT).show();
                break;
        }
        // Returning:
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openFragment(int pos) {
        // Initializing:
        TabLayout.Tab tab = binding.mainContentLayout.homeTabLayout.getTabAt(pos);
        // Developing:
        Objects.requireNonNull(tab).select();
    }


}