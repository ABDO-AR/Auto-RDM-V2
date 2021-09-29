package com.ar.team.company.app.autordm.model;

import android.graphics.drawable.Drawable;

@SuppressWarnings("unused")
public class Application {

    // Fields:
    private final String name;
    private final String packageName;
    private final Drawable icon;
    private final Drawable logo;
    // CheckingFields:
    private boolean checked = false;

    // Constructor:
    public Application(String name, String packageName, Drawable icon, Drawable logo) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.logo = logo;
    }

    // Setters:
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    // Getters:
    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Drawable getLogo() {
        return logo;
    }

    public boolean isChecked() {
        return checked;
    }
}
