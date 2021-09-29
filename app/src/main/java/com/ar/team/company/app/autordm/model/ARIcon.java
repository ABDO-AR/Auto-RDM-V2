package com.ar.team.company.app.autordm.model;

import android.graphics.drawable.Icon;

public class ARIcon {

    // Fields:
    private final String id;
    private final Icon icon;

    // Constructor:
    public ARIcon(String id, Icon icon) {
        this.id = id;
        this.icon = icon;
    }

    // Getters:
    public String getId() {
        return id;
    }

    public Icon getIcon() {
        return icon;
    }
}
