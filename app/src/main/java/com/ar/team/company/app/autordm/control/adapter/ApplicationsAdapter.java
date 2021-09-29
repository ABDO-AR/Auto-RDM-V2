package com.ar.team.company.app.autordm.control.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.team.company.app.autordm.annotations.ERROR;
import com.ar.team.company.app.autordm.annotations.UnderDevelopment;
import com.ar.team.company.app.autordm.control.preferences.ARPreferencesManager;
import com.ar.team.company.app.autordm.databinding.SingleAppItemBinding;
import com.ar.team.company.app.autordm.model.Application;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
@UnderDevelopment(development = "CheckBox In RecyclerView Enabled By It Self")
@ERROR(error = "(UNDER-DEVELOPMENT) THE SAME AS NEWS-X AND COMPLETE ALL SELECTED")
public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ApplicationsViewHolder> {

    // Fields:
    private final Context context;
    private final List<Application> applications;
    private final ARPreferencesManager manager;
    private final Apps apps;
    // Preferences:
    private String currentPackages;
    // TAGS:
    private static final String TAG = "ApplicationsAdapter";

    // Constructor:
    public ApplicationsAdapter(Context context, List<Application> applications, Apps apps) {
        // Initializing:
        this.context = context;
        this.applications = applications;
        this.manager = new ARPreferencesManager(context);
        // Interface:
        this.apps = apps;
        // Preferences:
        this.currentPackages = manager.getStringPreferences(ARPreferencesManager.PACKAGE_APP_NAME);
        // Notify:
        notifyDataSetChanged();
    }

    // Adapter:
    @NonNull
    @NotNull
    @Override
    public ApplicationsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Initializing:
        LayoutInflater inflater = LayoutInflater.from(context);
        SingleAppItemBinding binding = SingleAppItemBinding.inflate(inflater, parent, false);
        // Debugging:
        Log.d(TAG, "onCreateViewHolder: View Holder Created Successfully");
        // Returning:
        return new ApplicationsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ApplicationsAdapter.ApplicationsViewHolder holder, int position) {
        // Initializing:
        Application application = applications.get(position);
        Drawable appIcon = application.getIcon();
        // OurInterface:
        apps.onAppClicked(applications, position, holder.binding);
        // Developing:
        holder.binding.appIconImageView.setImageDrawable(appIcon);
        holder.binding.appNameTextView.setText(application.getName());
        // PreparingApplication:
        if (currentPackages.contains(application.getPackageName())) application.setChecked(true);
        // PreparingSwitch:
        if (application.isChecked()) holder.binding.singleAppSwitch.setChecked(true);
        // Checking Apps:
        holder.binding.singleAppSwitch.setOnCheckedChangeListener((compoundButton, b) -> appsListener(application, position, b));
        // Debugging:
        Log.d(TAG, "onBindViewHolder: View Holder Bind Successfully");
    }

    // OnApps change listening:
    private void appsListener(Application app, int pos, boolean checked) {
        // Developing:
        if (checked) {
            if (!currentPackages.contains(app.getPackageName())) {
                // Adding:
                currentPackages += app.getPackageName() + ",";
                // Setting:
                manager.setStringPreferences(ARPreferencesManager.PACKAGE_APP_NAME, currentPackages);
                // Checking:
                app.setChecked(true);
            }
        } else {
            if (currentPackages.contains(app.getPackageName())) {
                // Initializing:
                String filtersPackageName = currentPackages.replace(app.getPackageName() + ",", "");
                currentPackages = filtersPackageName; // Set the new filtered packages.
                // Developing:
                manager.setStringPreferences(ARPreferencesManager.PACKAGE_APP_NAME, filtersPackageName);
                // Checking:
                app.setChecked(false);
            }
        }
        // This line only for make things clear:
        Log.d(TAG, "appsListener: --------------------------------------------------------------");
        // Debugging:
        Log.d(TAG, "appsListener(SP): " + manager.getStringPreferences(ARPreferencesManager.PACKAGE_APP_NAME));
        Log.d(TAG, "appsListener(CP): " + currentPackages);
        // Notify:
       // notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    // Holders:
    static class ApplicationsViewHolder extends RecyclerView.ViewHolder {

        // Fields:
        private final SingleAppItemBinding binding;

        // Constructor:
        public ApplicationsViewHolder(@NonNull @NotNull SingleAppItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Getters:
        public SingleAppItemBinding getBinding() {
            return binding;
        }
    }

    // OurInterface:
    public interface Apps {
        void onAppClicked(List<Application> apps, int pos, SingleAppItemBinding appBinding);
    }

    // Getters:
    public Context getContext() {
        return context;
    }

    public List<Application> getApplications() {
        return applications;
    }
}
