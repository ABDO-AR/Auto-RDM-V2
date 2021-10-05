package com.ar.team.company.app.autordm.ui.activity.home;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ar.team.company.app.autordm.R;
import com.ar.team.company.app.autordm.ar.access.ARAccess;
import com.ar.team.company.app.autordm.ar.observer.ARFilesObserver;
import com.ar.team.company.app.autordm.control.adapter.HomeItemsAdapter;
import com.ar.team.company.app.autordm.control.adapter.PagerAdapter;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;

import com.ar.team.company.app.autordm.databinding.ActivityHomeBinding;
import com.ar.team.company.app.autordm.ui.activity.settings.SettingsActivity;
import com.ar.team.company.app.autordm.ui.interfaces.HomeItemClickListener;
import com.ar.team.company.app.autordm.ar.utils.ARUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;

import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class HomeActivity extends AppCompatActivity implements HomeItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

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
    private ActivityResultLauncher<String> launcher;
    // TempData:
    private Thread tempThread;
    // TAGS:
    private static final String TAG = "HomeActivity";

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
            // Observers:
            initObservers();
        } else launcher.launch(requestPermName);
    }

    private void initPermissionsAccess() {
        // Checking:
        if (ContextCompat.checkSelfPermission(this, requestPermName) == PackageManager.PERMISSION_GRANTED) {
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
        }
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
        // Initializing(UI):
        initUI();
        // Initializing(MEDIATOR):
        mediator = new TabLayoutMediator(binding.mainContentLayout.homeTabLayout, binding.mainContentLayout.homeViewPager, (tab, position) -> tab.setText(adapter.getHeaders(position)));
        // Initializing(FIELDS):
        adapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle(), this);
        // AttachMediator:
        binding.mainContentLayout.homeViewPager.setAdapter(adapter);
        if (!mediator.isAttached()) mediator.attach();
        // Selecting:
        openFragment(manager.getIntegerPreferences(ARPreferencesManager.FRAGMENT_STATE_NUMBER));
        // ManagerListener:
        manager.getPreferences().registerOnSharedPreferenceChangeListener(this);
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
    protected void onResume() {
        // Checking:
        if (mediator != null){
            // Selecting:
            openFragment(manager.getIntegerPreferences(ARPreferencesManager.FRAGMENT_STATE_NUMBER));
        }
        // Initializing(APP):
        initApp();
        // Super:
        super.onResume();
    }

    @Override
    protected void onStop() {
        // Dismiss:
        dialog.dismiss();
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

    // Initializing(UserInterface):
    private void initUI() {
        // Setting The New ActionBar:
        //setSupportActionBar(binding.mainContentLayout.toolbar);
        // Initializing:
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        // Developing Nav Drawer:
        // HomeItemsAdapter homeItemsAdapter = new HomeItemsAdapter(this);
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
            case R.id.menu_setting:
                ARUtils.runActivity(this, SettingsActivity.class);
                break;
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