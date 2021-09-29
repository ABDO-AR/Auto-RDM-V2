package com.ar.team.company.app.autordm.ui.activity.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.databinding.ActivitySettingsBinding;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class SettingsActivity extends AppCompatActivity {

    // This For Control The XML-Main Views:
    private ActivitySettingsBinding binding;
    private ARPreferencesManager manager;
    // Themes:
    private String[] themes;
    // TAGS:
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater()); // INFLATE THE LAYOUT.
        View view = binding.getRoot(); // GET ROOT [BY DEF(CONSTRAINT LAYOUT)].
        setContentView(view); // SET THE VIEW CONTENT TO THE (VIEW).
        // Initializing:
        checkThemeNow();
        manager = new ARPreferencesManager(this);
        themes = new String[]{"Light", "Dark"};
        // Developing:
        binding.backButton.setOnClickListener(v -> finish());


        binding.btnSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                manager.setBooleanPreferences(ARPreferencesManager.LIGHT_THEME, false);
            } else {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                manager.setBooleanPreferences(ARPreferencesManager.LIGHT_THEME, true);
            }

        });
    }

    // OnThemeClick:
    private void switchThemes(View view) {
        // Initializing:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boolean state = manager.getThemeBooleanPreferences(ARPreferencesManager.LIGHT_THEME);
        // Preparing:
        builder.setTitle("Choose Theme");
        builder.setSingleChoiceItems(themes, state ? 0 : 1, this::onThemeSelected);
        // Developing:
        builder.show();
    }

    private void onThemeSelected(DialogInterface dialogInterface, int i) {
        // Initializing:
        String chosenTheme = themes[i];
        boolean state = chosenTheme.equals("Light");
        // Developing:
        if (state) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        // SettingPreferences:
        manager.setBooleanPreferences(ARPreferencesManager.LIGHT_THEME, state);
    }

    void checkThemeNow()
    {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_NO:
                binding.btnSwitch.setChecked(false);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                binding.btnSwitch.setChecked(true);
                break;
        }
    }
}