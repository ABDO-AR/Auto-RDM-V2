package com.ar.team.company.app.autordm.model;

import android.graphics.drawable.Drawable;

public class Board {

    // Fields:
    private final Drawable drawable;
    private final String header, content;

    // Constructor:
    public Board(Drawable drawable, String header, String content) {
        this.drawable = drawable;
        this.header = header;
        this.content = content;
    }

    // Getters:
    public Drawable getDrawable() {
        return drawable;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }
}
